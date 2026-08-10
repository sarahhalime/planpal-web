package web;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import data_access.FileEventDataAccessObject;
import data_access.SqliteSocialDataAccessObject;
import data_access.SqliteUserDataAccessObject;
import entity.Activity;
import entity.Event;
import entity.EventSummary;
import entity.Expense;
import use_case.login.LoginInputData;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.login.LoginOutputData;
import use_case.who_owes_what.AttendeeBalanceOutputData;
import use_case.who_owes_what.WhoOwesWhatInputData;
import use_case.who_owes_what.WhoOwesWhatInteractor;
import use_case.who_owes_what.WhoOwesWhatOutputBoundary;
import use_case.who_owes_what.WhoOwesWhatOutputData;

/**
 * Turns http requests into use case calls and their results into json.
 *
 * <p>This is the web equivalent of the desktop controllers and presenters: it builds input
 * data, runs the same interactor the Swing application runs, and captures whatever the
 * interactor hands to its output boundary.</p>
 */
final class ApiHandler implements HttpHandler {

    private static final int OK = 200;
    private static final int BAD_REQUEST = 400;
    private static final int UNAUTHORIZED = 401;
    private static final int NOT_FOUND = 404;
    private static final int SERVER_ERROR = 500;

    private final SqliteUserDataAccessObject userDataAccess;
    private final FileEventDataAccessObject eventDataAccess;
    private final SqliteSocialDataAccessObject socialDataAccess;

    ApiHandler(SqliteUserDataAccessObject userDataAccess,
               FileEventDataAccessObject eventDataAccess,
               SqliteSocialDataAccessObject socialDataAccess) {
        this.userDataAccess = userDataAccess;
        this.eventDataAccess = eventDataAccess;
        this.socialDataAccess = socialDataAccess;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final String path = exchange.getRequestURI().getPath();

        try {
            if ("/api/login".equals(path)) {
                this.login(exchange);
            }
            else if ("/api/event".equals(path)) {
                this.event(exchange);
            }
            else {
                respond(exchange, NOT_FOUND, new JSONObject().put("error", "Unknown endpoint"));
            }
        }
        catch (final RuntimeException exception) {
            respond(exchange, SERVER_ERROR,
                    new JSONObject().put("error", String.valueOf(exception.getMessage())));
        }
    }

    /**
     * Signs a user in and returns their events, using the same login interactor as the
     * desktop application.
     *
     * @param exchange the request
     * @throws IOException when the response cannot be written
     */
    private void login(HttpExchange exchange) throws IOException {
        final JSONObject request = readJson(exchange);
        final String username = request.optString("username", "").trim();
        final String password = request.optString("password", "");

        if (username.isEmpty() || password.isEmpty()) {
            respond(exchange, BAD_REQUEST,
                    new JSONObject().put("error", "Username and password are required."));
            return;
        }

        final CapturedLogin captured = new CapturedLogin();
        new LoginInteractor(this.userDataAccess, this.eventDataAccess, captured)
                .execute(new LoginInputData(username, password.toCharArray()));

        if (captured.error != null) {
            respond(exchange, UNAUTHORIZED, new JSONObject().put("error", captured.error));
        }
        else {
            respond(exchange, OK, loginJson(captured.result));
        }
    }

    /**
     * Returns everything the dashboard shows for one event.
     *
     * @param exchange the request
     * @throws IOException when the response cannot be written
     */
    private void event(HttpExchange exchange) throws IOException {
        final Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());
        final String idText = query.getOrDefault("id", "");
        final int eventId;

        try {
            eventId = Integer.parseInt(idText);
        }
        catch (final NumberFormatException exception) {
            respond(exchange, BAD_REQUEST, new JSONObject().put("error", "A numeric id is required."));
            return;
        }

        final Event event;

        try {
            event = this.eventDataAccess.getEvent(eventId);
        }
        catch (final Exception exception) {
            respond(exchange, NOT_FOUND, new JSONObject().put("error", "That event was not found."));
            return;
        }

        respond(exchange, OK, eventJson(event));
    }

    private JSONObject eventJson(Event event) {
        final JSONObject json = new JSONObject();

        json.put("eventId", event.getEventId());
        json.put("eventName", event.getEventName());
        json.put("description", event.getEventDescription());
        json.put("location", event.getEventLocation());
        json.put("currency", event.getEventCurrency());
        json.put("budget", event.getEventBudget() == null ? 0.0 : event.getEventBudget());
        json.put("startDate", event.getStartDate());
        json.put("endDate", event.getEndDate());
        json.put("attendees", new JSONArray(event.getAttendeeUsernames()));

        final JSONArray activities = new JSONArray();
        for (final Activity activity : event.getActivityList()) {
            activities.put(new JSONObject()
                    .put("name", activity.getActivityName())
                    .put("date", activity.getDate())
                    .put("time", activity.getTime())
                    .put("location", activity.getLocation()));
        }
        json.put("activities", activities);

        final JSONArray expenses = new JSONArray();
        double spent = 0.0;
        for (final Expense expense : event.getExpenseList()) {
            spent += expense.getTotalAmount();
            expenses.put(new JSONObject()
                    .put("id", expense.getExpenseId())
                    .put("name", expense.getExpenseName())
                    .put("amount", expense.getTotalAmount())
                    .put("originalAmount", expense.getOriginalAmount())
                    .put("originalCurrency", expense.getOriginalCurrency())
                    .put("payer", expense.getPayerUsername())
                    .put("status", expense.getStatus())
                    .put("customSplit", expense.isCustomSplit())
                    .put("debtorCount", expense.getDebtors().size()));
        }
        json.put("expenses", expenses);
        json.put("spent", spent);

        json.put("balances", this.balancesJson(event.getEventId()));

        return json;
    }

    /**
     * Runs the who-owes-what interactor and returns each attendee's net balance.
     *
     * @param eventId the event to settle
     * @return the balances, or an empty array when they cannot be calculated
     */
    private JSONArray balancesJson(int eventId) {
        final CapturedBalances captured = new CapturedBalances();
        final JSONArray balances = new JSONArray();

        new WhoOwesWhatInteractor(
                this.eventDataAccess, captured, this.socialDataAccess::getProfilePicture)
                .execute(new WhoOwesWhatInputData(eventId));

        if (captured.result != null) {
            for (final AttendeeBalanceOutputData balance : captured.result.getAttendeeBalances()) {
                balances.put(new JSONObject()
                        .put("name", balance.getName())
                        .put("amount", balance.getTotalAmount())
                        .put("status", balance.getBalanceStatus()));
            }
        }
        return balances;
    }

    private static JSONObject loginJson(LoginOutputData data) {
        final JSONObject json = new JSONObject();

        json.put("username", data.getUsername());
        json.put("displayName", data.getDisplayName());
        json.put("preferredCurrency", data.getPreferredCurrency());

        final JSONArray events = new JSONArray();
        for (final EventSummary summary : data.getEvents()) {
            events.put(new JSONObject()
                    .put("eventId", summary.getEventId())
                    .put("eventName", summary.getEventName())
                    .put("dateInfo", summary.getDateTimeInfo()));
        }
        json.put("events", events);

        return json;
    }

    private static JSONObject readJson(HttpExchange exchange) throws IOException {
        final String body = new String(
                exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        final JSONObject json;

        if (body.isBlank()) {
            json = new JSONObject();
        }
        else {
            json = new JSONObject(body);
        }
        return json;
    }

    private static Map<String, String> parseQuery(String rawQuery) {
        final Map<String, String> values = new HashMap<>();

        if (rawQuery != null && !rawQuery.isBlank()) {
            for (final String pair : rawQuery.split("&")) {
                final int equals = pair.indexOf('=');
                if (equals > 0) {
                    values.put(pair.substring(0, equals), pair.substring(equals + 1));
                }
            }
        }
        return values;
    }

    private static void respond(HttpExchange exchange, int status, JSONObject body)
            throws IOException {
        final byte[] bytes = body.toString().getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    /** Captures whatever the login interactor reports. */
    private static final class CapturedLogin implements LoginOutputBoundary {
        private LoginOutputData result;
        private String error;

        @Override
        public void prepareSuccessView(LoginOutputData outputData) {
            this.result = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.error = errorMessage;
        }
    }

    /** Captures whatever the who-owes-what interactor reports. */
    private static final class CapturedBalances implements WhoOwesWhatOutputBoundary {
        private WhoOwesWhatOutputData result;

        @Override
        public void prepareSuccessView(WhoOwesWhatOutputData outputData) {
            this.result = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.result = null;
        }
    }
}

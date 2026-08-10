package web;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import data_access.EnvConfig;
import data_access.FileEventDataAccessObject;
import data_access.LocationInsightDataAccessObject;
import data_access.SqliteSocialDataAccessObject;
import data_access.SqliteUserDataAccessObject;
import data_access.ValhallaItineraryTravelGateway;
import data_access.WeatherDao;
import entity.Activity;
import entity.CommonEventFactory;
import entity.CommonExpenseFactory;
import entity.Event;
import entity.EventSummary;
import entity.Expense;
import use_case.add_expense.AddExpenseInputData;
import use_case.add_expense.AddExpenseInteractor;
import use_case.add_expense.AddExpenseOutputBoundary;
import use_case.add_expense.AddExpenseOutputData;
import use_case.itinerary.ItineraryInputData;
import use_case.itinerary.ItineraryInteractor;
import use_case.itinerary.ItineraryItemOutputData;
import use_case.itinerary.ItineraryOutputBoundary;
import use_case.itinerary.ItineraryOutputData;
import use_case.itinerary.ItineraryTravelOutputData;
import use_case.itinerary.ItineraryTravelStatus;
import use_case.location_insight.LocationInsightInputData;
import use_case.location_insight.LocationInsightInteractor;
import use_case.location_insight.LocationInsightOutputBoundary;
import use_case.location_insight.LocationInsightOutputData;
import use_case.login.LoginInputData;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.login.LoginOutputData;
import use_case.pay_expense.PayExpenseInputData;
import use_case.pay_expense.PayExpenseInteractor;
import use_case.pay_expense.PayExpenseOutputBoundary;
import use_case.pay_expense.PayExpenseOutputData;
import use_case.weather.ForecastDayOutputData;
import use_case.weather.WeatherInputData;
import use_case.weather.WeatherInteractor;
import use_case.weather.WeatherOutputBoundary;
import use_case.weather.WeatherOutputData;
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
            else if ("/api/expense/pay".equals(path)) {
                this.payExpense(exchange);
            }
            else if ("/api/expense/add".equals(path)) {
                this.addExpense(exchange);
            }
            else if ("/api/weather".equals(path)) {
                this.weather(exchange);
            }
            else if ("/api/insight".equals(path)) {
                this.insight(exchange);
            }
            else if ("/api/itinerary".equals(path)) {
                this.itinerary(exchange);
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

    /**
     * Marks an expense as settled, using the same pay-expense interactor as the desktop app.
     *
     * @param exchange the request
     * @throws IOException when the response cannot be written
     */
    private void payExpense(HttpExchange exchange) throws IOException {
        final JSONObject request = readJson(exchange);
        final int eventId = request.optInt("eventId", -1);
        final int expenseId = request.optInt("expenseId", -1);

        if (eventId < 0 || expenseId < 0) {
            respond(exchange, BAD_REQUEST,
                    new JSONObject().put("error", "An event id and expense id are required."));
            return;
        }

        final Captured captured = new Captured();
        new PayExpenseInteractor(this.eventDataAccess, new PayExpenseOutputBoundary() {
            @Override
            public void prepareSuccessView(PayExpenseOutputData outputData) {
                captured.ok = true;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                captured.error = errorMessage;
            }
        }).execute(new PayExpenseInputData(eventId, expenseId));

        this.respondToWrite(exchange, captured, eventId);
    }

    /**
     * Adds an expense, splitting it equally between the chosen debtors.
     *
     * @param exchange the request
     * @throws IOException when the response cannot be written
     */
    private void addExpense(HttpExchange exchange) throws IOException {
        final JSONObject request = readJson(exchange);
        final int eventId = request.optInt("eventId", -1);
        final String name = request.optString("name", "").trim();
        final String payer = request.optString("payer", "").trim();
        final double amount = request.optDouble("amount", 0.0);
        final Set<String> debtors = new LinkedHashSet<>();

        for (final Object debtor : request.optJSONArray("debtors") == null
                ? new JSONArray() : request.getJSONArray("debtors")) {
            debtors.add(String.valueOf(debtor));
        }

        if (eventId < 0 || name.isEmpty() || payer.isEmpty() || amount <= 0 || debtors.isEmpty()) {
            respond(exchange, BAD_REQUEST, new JSONObject().put(
                    "error", "A name, payer, positive amount and at least one person are required."));
            return;
        }

        final Captured captured = new Captured();
        new AddExpenseInteractor(this.eventDataAccess, new AddExpenseOutputBoundary() {
            @Override
            public void prepareSuccessView(AddExpenseOutputData outputData) {
                captured.ok = true;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                captured.error = errorMessage;
            }
        }, new CommonExpenseFactory(), new CommonEventFactory())
                .execute(new AddExpenseInputData(
                        eventId, name, payer, amount, false, debtors, Map.of()));

        this.respondToWrite(exchange, captured, eventId);
    }

    /**
     * Returns the refreshed event after a write, so the client redraws from stored state
     * rather than guessing what changed.
     *
     * @param exchange the request
     * @param captured what the interactor reported
     * @param eventId the event that was written to
     * @throws IOException when the response cannot be written
     */
    private void respondToWrite(HttpExchange exchange, Captured captured, int eventId)
            throws IOException {
        if (captured.error != null) {
            respond(exchange, BAD_REQUEST, new JSONObject().put("error", captured.error));
        }
        else {
            try {
                respond(exchange, OK, eventJson(this.eventDataAccess.getEvent(eventId)));
            }
            catch (final Exception exception) {
                respond(exchange, SERVER_ERROR,
                        new JSONObject().put("error", "Saved, but the event could not be reloaded."));
            }
        }
    }

    /**
     * Returns the forecast for an event's location and start date.
     *
     * <p>These three panels are requested separately from the event itself because each one
     * calls a remote service. Loading them alongside the dashboard would make every event
     * wait on the slowest network call, and a service being down would take the whole page
     * with it rather than just its own panel.</p>
     *
     * @param exchange the request
     * @throws IOException when the response cannot be written
     */
    private void weather(HttpExchange exchange) throws IOException {
        final Event event = this.requestedEvent(exchange);

        if (event == null) {
            return;
        }

        final Panel<WeatherOutputData> captured = new Panel<>();
        new WeatherInteractor(new WeatherDao(), new WeatherOutputBoundary() {
            @Override
            public void prepareSuccessView(WeatherOutputData outputData) {
                captured.result = outputData;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                captured.error = errorMessage;
            }
        }).execute(new WeatherInputData(event.getEventLocation(), LocalDate.parse(event.getStartDate())));

        if (captured.result == null) {
            respond(exchange, OK, new JSONObject().put("error", panelError(captured.error)));
            return;
        }

        final JSONArray forecast = new JSONArray();
        for (final ForecastDayOutputData day : captured.result.getForecast()) {
            forecast.put(new JSONObject()
                    .put("date", String.valueOf(day.getDate()))
                    .put("temperature", day.getTempAtDay())
                    .put("status", day.getWeatherStatusAtDay()));
        }

        respond(exchange, OK, new JSONObject()
                .put("location", captured.result.getLocation())
                .put("temperature", captured.result.getTemperature())
                .put("status", captured.result.getWeatherStatus())
                .put("precipitation", captured.result.getPrecipitationProbability())
                .put("wind", captured.result.getWindSpeed())
                .put("forecast", forecast));
    }

    /**
     * Returns the AI generated planning scores for an event's location.
     *
     * @param exchange the request
     * @throws IOException when the response cannot be written
     */
    private void insight(HttpExchange exchange) throws IOException {
        final Event event = this.requestedEvent(exchange);

        if (event == null) {
            return;
        }

        final Panel<LocationInsightOutputData> captured = new Panel<>();
        new LocationInsightInteractor(new LocationInsightDataAccessObject(),
                new LocationInsightOutputBoundary() {
                    @Override
                    public void prepareSuccessView(LocationInsightOutputData outputData) {
                        captured.result = outputData;
                    }

                    @Override
                    public void prepareFailView(String errorMessage) {
                        captured.error = errorMessage;
                    }
                }).execute(new LocationInsightInputData(event.getEventLocation()));

        if (captured.result == null) {
            respond(exchange, OK, new JSONObject().put("error", panelError(captured.error)));
        }
        else {
            respond(exchange, OK, new JSONObject()
                    .put("fun", captured.result.getFunScore())
                    .put("safety", captured.result.getSafetyScore())
                    .put("accessibility", captured.result.getAccessibilityScore())
                    .put("amenities", captured.result.getAmenitiesScore())
                    .put("affordability", captured.result.getAffordabilityScore())
                    .put("tags", new JSONArray(captured.result.getTagsList())));
        }
    }

    /**
     * Returns the event's activities as a timeline, with travel estimates between them.
     *
     * @param exchange the request
     * @throws IOException when the response cannot be written
     */
    private void itinerary(HttpExchange exchange) throws IOException {
        final Event event = this.requestedEvent(exchange);

        if (event == null) {
            return;
        }

        final Panel<ItineraryOutputData> captured = new Panel<>();
        new ItineraryInteractor(this.eventDataAccess::getEvent, new ItineraryOutputBoundary() {
            @Override
            public void prepareSuccessView(ItineraryOutputData outputData) {
                captured.result = outputData;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                captured.error = errorMessage;
            }
        }, new ValhallaItineraryTravelGateway(
                EnvConfig.get("GOOGLE_MAPS_API_KEY", "google.maps.api.key")))
                .execute(new ItineraryInputData(event.getEventId()));

        if (captured.result == null) {
            respond(exchange, OK, new JSONObject().put("error", panelError(captured.error)));
            return;
        }

        final JSONArray items = new JSONArray();
        for (final ItineraryItemOutputData item : captured.result.getItems()) {
            final JSONObject json = new JSONObject()
                    .put("name", item.getActivityName())
                    .put("date", item.getDate())
                    .put("time", item.getTime())
                    .put("location", item.getLocation());

            if (item.getTravelToNext() != null) {
                json.put("travel", travelSummary(item.getTravelToNext()));
            }
            items.put(json);
        }

        respond(exchange, OK, new JSONObject().put("items", items));
    }

    /**
     * Reads the event named by the {@code id} query parameter, answering the request itself
     * when the id is missing or unknown.
     *
     * @param exchange the request
     * @return the event, or null when a response has already been sent
     * @throws IOException when the response cannot be written
     */
    private Event requestedEvent(HttpExchange exchange) throws IOException {
        final Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());
        Event event = null;

        try {
            event = this.eventDataAccess.getEvent(Integer.parseInt(query.getOrDefault("id", "")));
        }
        catch (final NumberFormatException exception) {
            respond(exchange, BAD_REQUEST, new JSONObject().put("error", "A numeric id is required."));
        }
        catch (final Exception exception) {
            respond(exchange, NOT_FOUND, new JSONObject().put("error", "That event was not found."));
        }
        return event;
    }

    /**
     * Describes the journey to the next activity the way the desktop itinerary does:
     * how long it takes, and whether the gap in the schedule is enough for it.
     *
     * @param travel the estimate between two activities
     * @return a short human readable summary, or an empty string when there is nothing useful
     */
    private static String travelSummary(ItineraryTravelOutputData travel) {
        final StringBuilder summary = new StringBuilder();

        if (travel.getStatus() == ItineraryTravelStatus.UNAVAILABLE
                || travel.getStatus() == ItineraryTravelStatus.NO_SCHEDULE) {
            return "";
        }

        if (travel.getDrivingMinutes() > 0) {
            summary.append(travel.getDrivingMinutes()).append(" min drive");
        }
        else if (travel.getWalkingMinutes() > 0) {
            summary.append(travel.getWalkingMinutes()).append(" min walk");
        }

        if (travel.getStatus() == ItineraryTravelStatus.TIGHT) {
            summary.append(" · tight");
        }
        else if (travel.getStatus() == ItineraryTravelStatus.INSUFFICIENT) {
            summary.append(" · not enough time");
        }
        return summary.toString();
    }

    private static String panelError(String message) {
        final String text;

        if (message == null || message.isBlank()) {
            text = "Unavailable right now.";
        }
        else {
            text = message;
        }
        return text;
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

    /** Captures whatever a panel's interactor reports. */
    private static final class Panel<T> {
        private T result;
        private String error;
    }

    /** Captures whether a writing interactor succeeded, and why it did not. */
    private static final class Captured {
        private boolean ok;
        private String error;
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

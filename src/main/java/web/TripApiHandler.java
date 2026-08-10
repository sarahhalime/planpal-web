package web;

import java.io.IOException;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import data_access.EnvConfig;
import data_access.FileEventDataAccessObject;
import data_access.JsonEventFileDataAccessObject;
import data_access.OpenStreetMapEventMapGateway;
import data_access.SqliteSocialDataAccessObject;
import data_access.SqliteUserDataAccessObject;
import entity.CommonActivityFactory;
import entity.CommonEventFactory;
import entity.CommonExpenseFactory;
import entity.EventScheduleInput;
import entity.ExpensesData;
import use_case.add_activity.AddActivityInputData;
import use_case.add_activity.AddActivityInteractor;
import use_case.add_activity.AddActivityOutputBoundary;
import use_case.add_activity.AddActivityOutputData;
import use_case.add_guests.AddGuestInputData;
import use_case.add_guests.AddGuestInteractor;
import use_case.add_guests.AddGuestOutputBoundary;
import use_case.add_guests.AddGuestOutputData;
import use_case.create_event.CreateEventInputData;
import use_case.create_event.CreateEventInteractor;
import use_case.create_event.CreateEventOutputBoundary;
import use_case.create_event.CreateEventOutputData;
import use_case.delete_event.DeleteEventInputData;
import use_case.delete_event.DeleteEventInteractor;
import use_case.delete_event.DeleteEventOutputBoundary;
import use_case.delete_event.DeleteEventOutputData;
import use_case.edit_activity.EditActivityInputData;
import use_case.edit_activity.EditActivityInteractor;
import use_case.edit_activity.EditActivityOutputBoundary;
import use_case.edit_activity.EditActivityOutputData;
import use_case.edit_event.EditEventInputData;
import use_case.edit_event.EditEventInteractor;
import use_case.edit_event.EditEventOutputBoundary;
import use_case.edit_event.EditEventOutputData;
import use_case.edit_expense.EditExpenseInputData;
import use_case.edit_expense.EditExpenseInteractor;
import use_case.edit_expense.EditExpenseOutputBoundary;
import use_case.edit_expense.EditExpenseOutputData;
import use_case.event_map.EventMapInputData;
import use_case.event_map.EventMapInteractor;
import use_case.event_map.EventMapOutputBoundary;
import use_case.event_map.EventMapOutputData;
import use_case.event_map.EventMapRenderedPoint;
import use_case.event_photo.EventPhotoInputData;
import use_case.event_photo.EventPhotoInteractor;
import use_case.event_photo.EventPhotoOutputBoundary;
import use_case.event_photo.EventPhotoOutputData;
import use_case.remove_activity.RemoveActivityInputData;
import use_case.remove_activity.RemoveActivityInteractor;
import use_case.remove_activity.RemoveActivityOutputBoundary;
import use_case.remove_activity.RemoveActivityOutputData;
import use_case.remove_expense.RemoveExpenseInputData;
import use_case.remove_expense.RemoveExpenseInteractor;
import use_case.remove_expense.RemoveExpenseOutputBoundary;
import use_case.remove_expense.RemoveExpenseOutputData;
import use_case.remove_guest.RemoveGuestInputData;
import use_case.remove_guest.RemoveGuestInteractor;
import use_case.remove_guest.RemoveGuestOutputBoundary;
import use_case.remove_guest.RemoveGuestOutputData;
import use_case.save_event.SaveEventInputData;
import use_case.save_event.SaveEventInteractor;
import use_case.save_event.SaveEventOutputBoundary;
import use_case.save_event.SaveEventOutputData;
import use_case.view_attendees.ViewAttendeeData;
import use_case.view_attendees.ViewAttendeesInputData;
import use_case.view_attendees.ViewAttendeesInteractor;
import use_case.view_attendees.ViewAttendeesOutputBoundary;
import use_case.view_attendees.ViewAttendeesOutputData;

/**
 * Creating and changing trips: the event itself, its activities, its expenses and its
 * guest list.
 */
final class TripApiHandler implements HttpHandler {

    private final FileEventDataAccessObject eventDataAccess;
    private final SqliteUserDataAccessObject userDataAccess;
    private final SqliteSocialDataAccessObject socialDataAccess;
    private final CommonEventFactory eventFactory = new CommonEventFactory();
    private final CommonActivityFactory activityFactory = new CommonActivityFactory();
    private final CommonExpenseFactory expenseFactory = new CommonExpenseFactory();

    TripApiHandler(FileEventDataAccessObject eventDataAccess,
                   SqliteUserDataAccessObject userDataAccess,
                   SqliteSocialDataAccessObject socialDataAccess) {
        this.eventDataAccess = eventDataAccess;
        this.userDataAccess = userDataAccess;
        this.socialDataAccess = socialDataAccess;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            this.route(exchange.getRequestURI().getPath(), exchange);
        }
        catch (final RuntimeException exception) {
            Json.fail(exchange, Json.SERVER_ERROR, String.valueOf(exception.getMessage()));
        }
    }

    private void route(String path, HttpExchange exchange) throws IOException {
        switch (path) {
            case "/api/trip/create" -> this.createEvent(exchange);
            case "/api/trip/edit" -> this.editEvent(exchange);
            case "/api/trip/delete" -> this.deleteEvent(exchange);
            case "/api/trip/activity/add" -> this.addActivity(exchange);
            case "/api/trip/activity/edit" -> this.editActivity(exchange);
            case "/api/trip/activity/remove" -> this.removeActivity(exchange);
            case "/api/trip/expense/remove" -> this.removeExpense(exchange);
            case "/api/trip/attendees" -> this.attendees(exchange);
            case "/api/trip/guest/add" -> this.addGuest(exchange);
            case "/api/trip/guest/remove" -> this.removeGuest(exchange);
            case "/api/trip/expense/edit" -> this.editExpense(exchange);
            case "/api/trip/map" -> this.map(exchange);
            case "/api/trip/photo" -> this.photo(exchange);
            case "/api/trip/report" -> this.report(exchange);
            default -> Json.fail(exchange, Json.NOT_FOUND, "Unknown endpoint");
        }
    }

    private void createEvent(HttpExchange exchange) throws IOException {
        final JSONObject body = Json.read(exchange);
        final Result<CreateEventOutputData> captured = new Result<>();

        new CreateEventInteractor(this.eventDataAccess, new CreateEventOutputBoundary() {
            @Override
            public void prepareSuccessView(CreateEventOutputData outputData) {
                captured.value = outputData;
            }

            @Override
            public void prepareFailView(String errorMessage,
                                        use_case.create_event.CreateEventErrorField errorField) {
                captured.error = errorMessage;
            }
        }, this.eventFactory).execute(new CreateEventInputData(
                body.optString("username", ""),
                body.optString("name", ""),
                body.optString("description", ""),
                body.optString("location", ""),
                body.optDouble("budget", 0.0),
                body.optString("currency", "CAD"),
                schedule(body)));

        Json.result(exchange, captured.error, new JSONObject().put("ok", true));
    }

    private void editEvent(HttpExchange exchange) throws IOException {
        final JSONObject body = Json.read(exchange);
        final Result<EditEventOutputData> captured = new Result<>();

        new EditEventInteractor(this.eventDataAccess, new EditEventOutputBoundary() {
            @Override
            public void prepareSuccessView(EditEventOutputData outputData) {
                captured.value = outputData;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                captured.error = errorMessage;
            }
        }, this.eventFactory).execute(new EditEventInputData(
                body.optInt("eventId", -1),
                body.optString("name", ""),
                body.optString("description", ""),
                body.optString("location", ""),
                body.optDouble("budget", 0.0),
                schedule(body)));

        this.respondWithEvent(exchange, captured.error, body.optInt("eventId", -1));
    }

    private void deleteEvent(HttpExchange exchange) throws IOException {
        final JSONObject body = Json.read(exchange);
        final Result<DeleteEventOutputData> captured = new Result<>();

        new DeleteEventInteractor(this.eventDataAccess, new DeleteEventOutputBoundary() {
            @Override
            public void prepareSuccessView(DeleteEventOutputData outputData) {
                captured.value = outputData;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                captured.error = errorMessage;
            }
        }).execute(new DeleteEventInputData(
                body.optInt("eventId", -1), body.optString("username", "")));

        Json.result(exchange, captured.error, new JSONObject().put("ok", true));
    }

    private void addActivity(HttpExchange exchange) throws IOException {
        final JSONObject body = Json.read(exchange);
        final Result<AddActivityOutputData> captured = new Result<>();

        new AddActivityInteractor(this.eventDataAccess, new AddActivityOutputBoundary() {
            @Override
            public void prepareSuccessView(AddActivityOutputData outputData) {
                captured.value = outputData;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                captured.error = errorMessage;
            }
        }, this.activityFactory, this.eventFactory).execute(new AddActivityInputData(
                body.optInt("eventId", -1),
                body.optString("name", ""),
                body.optString("date", ""),
                body.optString("time", ""),
                body.optString("location", "")));

        this.respondWithEvent(exchange, captured.error, body.optInt("eventId", -1));
    }

    private void editActivity(HttpExchange exchange) throws IOException {
        final JSONObject body = Json.read(exchange);
        final Result<EditActivityOutputData> captured = new Result<>();

        new EditActivityInteractor(this.eventDataAccess, new EditActivityOutputBoundary() {
            @Override
            public void prepareSuccessView(EditActivityOutputData outputData) {
                captured.value = outputData;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                captured.error = errorMessage;
            }
        }, this.activityFactory, this.eventFactory).execute(new EditActivityInputData(
                body.optInt("eventId", -1),
                body.optInt("index", -1),
                body.optString("name", ""),
                body.optString("date", ""),
                body.optString("time", ""),
                body.optString("location", "")));

        this.respondWithEvent(exchange, captured.error, body.optInt("eventId", -1));
    }

    private void removeActivity(HttpExchange exchange) throws IOException {
        final JSONObject body = Json.read(exchange);
        final Result<RemoveActivityOutputData> captured = new Result<>();

        new RemoveActivityInteractor(this.eventDataAccess, new RemoveActivityOutputBoundary() {
            @Override
            public void prepareSuccessView(RemoveActivityOutputData outputData) {
                captured.value = outputData;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                captured.error = errorMessage;
            }
        }, this.eventFactory).execute(new RemoveActivityInputData(
                body.optInt("eventId", -1), body.optInt("index", -1)));

        this.respondWithEvent(exchange, captured.error, body.optInt("eventId", -1));
    }

    private void removeExpense(HttpExchange exchange) throws IOException {
        final JSONObject body = Json.read(exchange);
        final Result<RemoveExpenseOutputData> captured = new Result<>();

        new RemoveExpenseInteractor(this.eventDataAccess, new RemoveExpenseOutputBoundary() {
            @Override
            public void prepareSuccessView(RemoveExpenseOutputData outputData) {
                captured.value = outputData;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                captured.error = errorMessage;
            }
        }, this.eventFactory).execute(new RemoveExpenseInputData(
                body.optInt("eventId", -1), body.optInt("expenseId", -1)));

        this.respondWithEvent(exchange, captured.error, body.optInt("eventId", -1));
    }

    private void attendees(HttpExchange exchange) throws IOException {
        final Result<ViewAttendeesOutputData> captured = new Result<>();
        final int eventId = Integer.parseInt(Json.query(exchange, "id"));

        new ViewAttendeesInteractor(this.eventDataAccess::getEvent,
                this.userDataAccess::getDisplayName,
                this.socialDataAccess::getProfilePicture, new ViewAttendeesOutputBoundary() {
                    @Override
                    public void prepareSuccessView(ViewAttendeesOutputData outputData) {
                        captured.value = outputData;
                    }

                    @Override
                    public void prepareFailView(String errorMessage) {
                        captured.error = errorMessage;
                    }
                }).execute(new ViewAttendeesInputData(eventId));

        if (captured.error != null) {
            Json.fail(exchange, Json.BAD_REQUEST, captured.error);
            return;
        }

        final JSONArray people = new JSONArray();
        for (final ViewAttendeeData attendee : captured.value.getAttendees()) {
            people.put(new JSONObject()
                    .put("username", attendee.getUsername())
                    .put("displayName", attendee.getDisplayName())
                    .put("picture", attendee.getProfilePicture() == null
                            ? "" : Base64.getEncoder().encodeToString(attendee.getProfilePicture())));
        }
        Json.ok(exchange, new JSONObject().put("attendees", people));
    }

    private void addGuest(HttpExchange exchange) throws IOException {
        final JSONObject body = Json.read(exchange);
        final Result<AddGuestOutputData> captured = new Result<>();

        new AddGuestInteractor(this.eventDataAccess, this.userDataAccess,
                new AddGuestOutputBoundary() {
                    @Override
                    public void prepareSuccessView(AddGuestOutputData outputData) {
                        captured.value = outputData;
                    }

                    @Override
                    public void prepareFailView(String errorMessage) {
                        captured.error = errorMessage;
                    }

                    @Override
                    public void setAvaliableUsernames(java.util.List<String> usernames) {
                        // The web client searches for users separately.
                    }
                }, this.eventFactory).execute(new AddGuestInputData(
                        body.optInt("eventId", -1), body.optString("username", "")));

        this.respondWithEvent(exchange, captured.error, body.optInt("eventId", -1));
    }

    private void removeGuest(HttpExchange exchange) throws IOException {
        final JSONObject body = Json.read(exchange);
        final Result<RemoveGuestOutputData> captured = new Result<>();

        new RemoveGuestInteractor(this.eventDataAccess, new RemoveGuestOutputBoundary() {
            @Override
            public void prepareSuccessView(RemoveGuestOutputData outputData) {
                captured.value = outputData;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                captured.error = errorMessage;
            }
        }, this.eventFactory).execute(new RemoveGuestInputData(
                body.optInt("eventId", -1), body.optString("username", "")));

        this.respondWithEvent(exchange, captured.error, body.optInt("eventId", -1));
    }

    /**
     * Edits an expense. The three prefill callbacks on the output boundary are defaults, so
     * only success and failure need answering here.
     *
     * @param exchange the request
     * @throws IOException when the response cannot be written
     */
    private void editExpense(HttpExchange exchange) throws IOException {
        final JSONObject body = Json.read(exchange);
        final Result<EditExpenseOutputData> captured = new Result<>();
        final java.util.Set<String> debtors = new java.util.LinkedHashSet<>();

        for (final Object debtor : body.optJSONArray("debtors") == null
                ? new JSONArray() : body.getJSONArray("debtors")) {
            debtors.add(String.valueOf(debtor));
        }

        final ExpensesData expense = new ExpensesData(
                body.optInt("expenseId", -1),
                body.optString("name", ""),
                body.optDouble("amount", 0.0),
                body.optString("payer", ""),
                "",
                body.optString("status", "UNPAID"),
                "");

        new EditExpenseInteractor(this.eventDataAccess, new EditExpenseOutputBoundary() {
            @Override
            public void prepareSuccessView(EditExpenseOutputData outputData) {
                captured.value = outputData;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                captured.error = errorMessage;
            }
        }, this.expenseFactory, this.eventFactory).execute(new EditExpenseInputData(
                body.optInt("eventId", -1), expense, false, debtors, java.util.Map.of()));

        this.respondWithEvent(exchange, captured.error, body.optInt("eventId", -1));
    }

    /**
     * Returns the event and its activities as map points the browser can plot.
     *
     * @param exchange the request
     * @throws IOException when the response cannot be written
     */
    private void map(HttpExchange exchange) throws IOException {
        final Result<EventMapOutputData> captured = new Result<>();
        final int eventId = Integer.parseInt(Json.query(exchange, "id"));

        new EventMapInteractor(this.eventDataAccess::getEvent,
                new OpenStreetMapEventMapGateway(
                        EnvConfig.get("GOOGLE_MAPS_API_KEY", "google.maps.api.key")),
                new EventMapOutputBoundary() {
                    @Override
                    public void prepareSuccessView(EventMapOutputData outputData) {
                        captured.value = outputData;
                    }

                    @Override
                    public void prepareFailView(String errorMessage) {
                        captured.error = errorMessage;
                    }
                }).execute(new EventMapInputData(eventId));

        if (captured.value == null) {
            Json.ok(exchange, new JSONObject().put("error",
                    captured.error == null ? "The map is unavailable." : captured.error));
            return;
        }

        final JSONArray points = new JSONArray();
        for (final EventMapRenderedPoint point : captured.value.getPoints()) {
            points.put(new JSONObject()
                    .put("title", point.getLocation().getTitle())
                    .put("address", point.getLocation().getAddress())
                    .put("latitude", point.getLatitude())
                    .put("longitude", point.getLongitude())
                    .put("isEvent", point.getLocation().isEventLocation()));
        }
        Json.ok(exchange, new JSONObject().put("points", points));
    }

    /**
     * Stores a photo for the event.
     *
     * @param exchange the request
     * @throws IOException when the response cannot be written
     */
    private void photo(HttpExchange exchange) throws IOException {
        final JSONObject body = Json.read(exchange);
        final Result<EventPhotoOutputData> captured = new Result<>();

        new EventPhotoInteractor(this.eventDataAccess, new EventPhotoOutputBoundary() {
            @Override
            public void prepareSuccessView(EventPhotoOutputData outputData) {
                captured.value = outputData;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                captured.error = errorMessage;
            }
        }).execute(new EventPhotoInputData(body.optInt("eventId", -1),
                Base64.getDecoder().decode(body.optString("photo", ""))));

        this.respondWithEvent(exchange, captured.error, body.optInt("eventId", -1));
    }

    /**
     * Writes the event report to a temporary file and sends it back as a download, since a
     * browser cannot be handed a path on the server's disk.
     *
     * @param exchange the request
     * @throws IOException when the response cannot be written
     */
    private void report(HttpExchange exchange) throws IOException {
        final int eventId = Integer.parseInt(Json.query(exchange, "id"));
        final String username = Json.query(exchange, "username");
        final java.io.File file = java.io.File.createTempFile("planpal-event-", ".json");
        final Result<SaveEventOutputData> captured = new Result<>();

        new SaveEventInteractor(new JsonEventFileDataAccessObject(this.eventDataAccess),
                new SaveEventOutputBoundary() {
                    @Override
                    public void prepareSuccessView(SaveEventOutputData outputData) {
                        captured.value = outputData;
                    }

                    @Override
                    public void prepareFailView(String errorMessage) {
                        captured.error = errorMessage;
                    }
                }).execute(new SaveEventInputData(username, eventId, file.getAbsolutePath()));

        if (captured.error != null) {
            Json.fail(exchange, Json.BAD_REQUEST, captured.error);
            return;
        }

        final byte[] body = java.nio.file.Files.readAllBytes(file.toPath());
        java.nio.file.Files.deleteIfExists(file.toPath());

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.getResponseHeaders().add("Content-Disposition",
                "attachment; filename=\"planpal-event-" + eventId + ".json\"");
        exchange.sendResponseHeaders(Json.OK, body.length);
        try (java.io.OutputStream out = exchange.getResponseBody()) {
            out.write(body);
        }
    }

    private static EventScheduleInput schedule(JSONObject body) {
        return new EventScheduleInput(
                body.optString("startDate", ""),
                body.optString("startTime", ""),
                body.optString("endDate", ""),
                body.optString("endTime", ""));
    }

    /**
     * Returns the reloaded event after a change, so the client redraws from stored state.
     *
     * @param exchange the request
     * @param error what the interactor reported, or null
     * @param eventId the event that was changed
     * @throws IOException when the response cannot be written
     */
    private void respondWithEvent(HttpExchange exchange, String error, int eventId)
            throws IOException {
        if (error != null) {
            Json.fail(exchange, Json.BAD_REQUEST, error);
            return;
        }

        try {
            Json.ok(exchange, EventJson.of(this.eventDataAccess.getEvent(eventId),
                    this.eventDataAccess, this.socialDataAccess));
        }
        catch (final Exception exception) {
            Json.fail(exchange, Json.SERVER_ERROR, "Saved, but the event could not be reloaded.");
        }
    }

    /** Captures whatever an interactor reports. */
    private static final class Result<T> {
        private T value;
        private String error;
    }
}

package web;

import java.io.IOException;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import data_access.FileEventDataAccessObject;
import data_access.SqliteSocialDataAccessObject;
import data_access.SqliteUserDataAccessObject;
import entity.CommonActivityFactory;
import entity.CommonEventFactory;
import entity.CommonExpenseFactory;
import entity.EventScheduleInput;
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

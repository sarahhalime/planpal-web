package web;

import org.json.JSONArray;
import org.json.JSONObject;

import data_access.FileEventDataAccessObject;
import data_access.SqliteSocialDataAccessObject;
import entity.Activity;
import entity.Event;
import entity.Expense;
import use_case.who_owes_what.AttendeeBalanceOutputData;
import use_case.who_owes_what.WhoOwesWhatInputData;
import use_case.who_owes_what.WhoOwesWhatInteractor;
import use_case.who_owes_what.WhoOwesWhatOutputBoundary;
import use_case.who_owes_what.WhoOwesWhatOutputData;

/**
 * Renders one event the way the dashboard needs it, so every endpoint that changes a trip
 * can hand back the same shape.
 */
final class EventJson {

    private EventJson() {
    }

    /**
     * Describes an event, its activities, its expenses and the resulting balances.
     *
     * @param event the event to describe
     * @param eventDataAccess used to settle the balances
     * @param socialDataAccess supplies attendee profile pictures
     * @return the event as json
     */
    static JSONObject of(Event event, FileEventDataAccessObject eventDataAccess,
                         SqliteSocialDataAccessObject socialDataAccess) {
        final JSONObject json = new JSONObject();

        json.put("eventId", event.getEventId());
        json.put("eventName", event.getEventName());
        json.put("description", event.getEventDescription());
        json.put("location", event.getEventLocation());
        json.put("currency", event.getEventCurrency());
        json.put("budget", event.getEventBudget() == null ? 0.0 : event.getEventBudget());
        json.put("startDate", event.getStartDate());
        json.put("startTime", event.getStartTime());
        json.put("endDate", event.getEndDate());
        json.put("endTime", event.getEndTime());
        json.put("attendees", new JSONArray(event.getAttendeeUsernames()));

        final JSONArray activities = new JSONArray();
        int index = 0;
        for (final Activity activity : event.getActivityList()) {
            activities.put(new JSONObject()
                    .put("index", index)
                    .put("name", activity.getActivityName())
                    .put("date", activity.getDate())
                    .put("time", activity.getTime())
                    .put("location", activity.getLocation()));
            index++;
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
        json.put("balances", balances(event.getEventId(), eventDataAccess, socialDataAccess));

        return json;
    }

    private static JSONArray balances(int eventId, FileEventDataAccessObject eventDataAccess,
                                      SqliteSocialDataAccessObject socialDataAccess) {
        final Captured captured = new Captured();
        final JSONArray balances = new JSONArray();

        new WhoOwesWhatInteractor(eventDataAccess, captured, socialDataAccess::getProfilePicture)
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

    /** Captures whatever the who-owes-what interactor reports. */
    private static final class Captured implements WhoOwesWhatOutputBoundary {
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

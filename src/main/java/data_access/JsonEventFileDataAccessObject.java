package data_access;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.json.JSONArray;
import org.json.JSONObject;

import entity.Event;
import entity.Expense;
import use_case.save_event.SaveEventDataAccessInterface;
import use_case.save_event.SaveEventDataException;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;
import use_case.who_owes_what.WhoOwesWhatDataAccessInterface;

/**
 * Saves event reports as JSON files.
 */
public final class JsonEventFileDataAccessObject
        implements SaveEventDataAccessInterface {

    private static final int JSON_INDENT = 4;

    private static final String NAME = "name";

    private final WhoOwesWhatDataAccessInterface
            eventDataAccessObject;

    /**
     * Constructs a JSON event-file data-access object.
     *
     * @param eventDataAccessObject the source of stored events
     */
    public JsonEventFileDataAccessObject(
            WhoOwesWhatDataAccessInterface eventDataAccessObject
    ) {
        this.eventDataAccessObject = eventDataAccessObject;
    }

    @Override
    public String saveEvent(
            String username,
            int eventId,
            String filePath
    ) throws SaveEventDataException {
        try {
            final Event event =
                    this.eventDataAccessObject.getEvent(eventId);
            final JSONObject report =
                    this.createEventReport(username, event);
            final File destination = new File(filePath);

            Files.writeString(
                    destination.toPath(),
                    report.toString(JSON_INDENT)
            );

            return event.getEventName();
        }
        catch (final IOException
                     | WhoOwesWhatDataAccessException exception) {
            throw new SaveEventDataException(
                    "The event could not be saved "
                            + "to the selected file."
            );
        }
    }

    private JSONObject createEventReport(
            String username,
            Event event
    ) {
        final JSONObject report = new JSONObject();

        report.put(
                "savedBy",
                this.toJsonValue(username)
        );
        report.put("eventId", event.getEventId());
        report.put(NAME, this.toJsonValue(event.getEventName())
        );
        report.put(
                "description",
                this.toJsonValue(
                        event.getEventDescription()
                )
        );
        report.put(
                "budget",
                this.toJsonValue(event.getEventBudget())
        );
        report.put(
                "location",
                this.toJsonValue(event.getEventLocation())
        );
        report.put(
                "startDate",
                this.toJsonValue(event.getStartDate())
        );
        report.put(
                "startTime",
                this.toJsonValue(event.getStartTime())
        );
        report.put(
                "endDate",
                this.toJsonValue(
                        event.getEventSchedule().getEndDate()
                )
        );

        report.put(
                "endTime",
                this.toJsonValue(
                        event.getEventSchedule().getEndTime()
                )
        );
        report.put(
                "attendees",
                this.createAttendeeArray(event)
        );
        report.put(
                "expenses",
                this.createExpenseArray(event)
        );

        return report;
    }

    private JSONArray createAttendeeArray(Event event) {
        final JSONArray attendees = new JSONArray();

        // TODO: NEEDS FIXING
        /*
        for (final Attendee attendee
                : event.getAttendeeList()) {
            final JSONObject attendeeData =
                    new JSONObject();

            attendeeData.put(
                    "attendeeId",
                    attendee.getAttendeeId()
            );
            attendeeData.put(NAME, this.toJsonValue(attendee.getName())
            );
            attendees.put(attendeeData);
        }

         */

        return attendees;
    }

    private JSONArray createExpenseArray(Event event) {
        final JSONArray expenses = new JSONArray();

        for (final Expense expense
                : event.getExpenseList()) {
            expenses.put(
                    this.createExpenseData(expense)
            );
        }

        return expenses;
    }

    private JSONObject createExpenseData(
            Expense expense
    ) {
        final JSONObject expenseData = new JSONObject();

        expenseData.put("expenseId", expense.getExpenseId());
        expenseData.put(NAME, this.toJsonValue(expense.getExpenseName()));
        expenseData.put("payerId", expense.getPayerUsername());
        expenseData.put("totalAmount", expense.getTotalAmount());
        expenseData.put("shares", this.createShareArray(expense));

        return expenseData;
    }

    private JSONArray createShareArray(
            Expense expense
    ) {
        final JSONArray shares = new JSONArray();

        // TODO: NEEDS FIXING
        /*
        for (final ExpenseShare share
                : expense.getExpenseShareList()) {
            final JSONObject shareData =
                    new JSONObject();

            shareData.put(
                    "attendeeId",
                    share.getAttendeeId()
            );
            shareData.put(
                    "shareAmount",
                    share.getShareAmount()
            );
            shares.put(shareData);
        }

         */

        return shares;
    }

    private Object toJsonValue(Object value) {
        final Object jsonValue;

        if (value == null) {
            jsonValue = JSONObject.NULL;
        }
        else {
            jsonValue = value;
        }

        return jsonValue;
    }
}

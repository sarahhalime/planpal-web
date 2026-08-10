package data_access;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import entity.Event;
import use_case.add_activity.AddActivityDataAccessInterface;
import use_case.add_expense.AddExpenseDataAccessInterface;
import use_case.add_guests.AddGuestDataAccessInterface;
import use_case.create_event.CreateEventDataAccessInterface;
import use_case.edit_activity.EditActivityDataAccessInterface;
import use_case.edit_event.EditEventDataAccessInterface;
import use_case.edit_expense.EditExpenseDataAccessInterface;
import use_case.pay_expense.PayExpenseDataAccessInterface;
import use_case.remove_activity.RemoveActivityDataAccessInterface;
import use_case.remove_expense.RemoveExpenseDataAccessInterface;
import use_case.remove_guest.RemoveGuestDataAccessInterface;
import use_case.select_event.SelectEventDataAccessInterface;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;
import use_case.who_owes_what.WhoOwesWhatDataAccessInterface;

/**
 * Shared in-memory event store used by event-related use cases.
 */
public class InMemoryEventDataAccessObject implements
        WhoOwesWhatDataAccessInterface,
        AddActivityDataAccessInterface,
        EditActivityDataAccessInterface,
        RemoveActivityDataAccessInterface,
        AddExpenseDataAccessInterface,
        EditExpenseDataAccessInterface,
        RemoveExpenseDataAccessInterface,
        PayExpenseDataAccessInterface,
        CreateEventDataAccessInterface,
        EditEventDataAccessInterface,
        AddGuestDataAccessInterface,
        RemoveGuestDataAccessInterface,
        SelectEventDataAccessInterface {

    private static final int FIRST_EVENT_ID = 1;

    private final Map<Integer, Event> events = new ConcurrentHashMap<>();

    /**
     * Returns the next available event ID.
     *
     * @return one more than the highest existing event ID
     */
    @Override
    public int getNextEventId() {
        int nextEventId = FIRST_EVENT_ID;

        for (final int eventId : this.events.keySet()) {
            if (eventId >= nextEventId) {
                nextEventId = eventId + FIRST_EVENT_ID;
            }
        }

        return nextEventId;
    }

    /**
     * Stores or replaces an event using its event ID.
     *
     * @param event the event to store
     */
    @Override
    public void saveEvent(Event event) {
        this.events.put(event.getEventId(), event);
    }

    /**
     * Checks whether a user is attending an event.
     *
     * @param eventId the event ID
     * @param username the username to check
     * @return true when the event exists and contains the username
     */
    @Override
    public boolean isAttendingEvent(int eventId, String username) {
        final Event event = this.events.get(eventId);
        return event != null && event.getAttendeeUsernames().contains(username);
    }

    /**
     * Retrieves an event by its ID.
     *
     * @param eventId the ID of the event
     * @return the requested event
     * @throws WhoOwesWhatDataAccessException when the event does not exist
     */
    @Override
    public Event getEvent(int eventId) throws WhoOwesWhatDataAccessException {
        final Event event = this.events.get(eventId);

        if (event == null) {
            throw new WhoOwesWhatDataAccessException(
                    "No event was found with ID " + eventId + "."
            );
        }

        return event;
    }
}

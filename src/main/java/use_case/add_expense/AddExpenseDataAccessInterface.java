package use_case.add_expense;

import entity.Event;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/**
 * Data access for the Add Expense use case.
 * Reads and writes events in the shared event store so an added expense is
 * reflected in the balances.
 */
public interface AddExpenseDataAccessInterface {

    /**
     * This method retrieves an event by its id.
     * @param eventId the id of the event
     * @return the requested event
     * @throws WhoOwesWhatDataAccessException if the event does not exist
     */
    Event getEvent(int eventId) throws WhoOwesWhatDataAccessException;

    /**
     * This method stores or replaces an event (used to save the event with its new expense).
     * @param event the event to store
     */
    void saveEvent(Event event);

    /**
     * Checks if the user is attending the event.
     * @param eventId the id of the event
     * @param username the username of the user
     * @return true if the user is attending the event, false otherwise
     */
    boolean isAttendingEvent(int eventId, String username);
}

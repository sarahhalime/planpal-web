package use_case.remove_expense;

import entity.Event;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/**
 * The data access interface for the remove expense use case.
 * This interface defines the methods for reading and writing events in the shared event store.
 */
public interface RemoveExpenseDataAccessInterface {

    /**
     * This method retrieves an event by its id.
     * @param eventId the id of the event
     * @return the requested event
     * @throws WhoOwesWhatDataAccessException if the event does not exist
     */
    Event getEvent(int eventId) throws WhoOwesWhatDataAccessException;

    /**
     * This method stores or replaces an event.
     * @param event the event to store
     */
    void saveEvent(Event event);
}

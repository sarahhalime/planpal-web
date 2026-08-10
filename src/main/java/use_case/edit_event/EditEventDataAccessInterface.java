package use_case.edit_event;

import entity.Event;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/**
 * The data access interface for the edit event use case.
 */
public interface EditEventDataAccessInterface {
    /**
     * Retrieves an event by its ID.
     *
     * @param eventId the ID of the event
     * @return the requested event
     * @throws WhoOwesWhatDataAccessException if the event does not exist
     */
    Event getEvent(int eventId)
            throws WhoOwesWhatDataAccessException;

    /**
     * Stores or replaces an event.
     *
     * @param event the event to store
     */
    void saveEvent(Event event);
}

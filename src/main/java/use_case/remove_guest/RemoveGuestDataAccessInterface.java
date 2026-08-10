package use_case.remove_guest;

import entity.Event;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/**
 * The data access for the remove guest use case.
 * This will read and write events in the shared event store so a removed attendee is
 * reflected everywhere the event is used.
 */
public interface RemoveGuestDataAccessInterface {

    /**
     * This method retrieves an event by its id.
     * @param eventId the id of the event
     * @return the requested event
     * @throws WhoOwesWhatDataAccessException if the event does not exist
     */
    Event getEvent(int eventId) throws WhoOwesWhatDataAccessException;

    /**
     * This method stores or replaces an event (used to save the event without the removed attendee).
     * @param event the event to store
     */
    void saveEvent(Event event);
}

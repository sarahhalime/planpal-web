package use_case.select_event;

import entity.Event;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/**
 * Data access for the Select Event use case.
 */
public interface SelectEventDataAccessInterface {

    /**
     * Retrieves an event by its id.
     * @param eventId the id of the event
     * @return the requested event
     * @throws WhoOwesWhatDataAccessException if the event does not exist
     */
    Event getEvent(int eventId) throws WhoOwesWhatDataAccessException;

    /**
     * Returns the stored photo for an event.
     *
     * @param eventId event identifier
     * @return photo bytes, or null when no photo is stored
     */
    default byte[] getEventPhoto(int eventId) {
        return null;
    }
}

package use_case.add_guests;

import entity.Event;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/**
 * The data access for the add guest use case.
 * This will read and write events in the shared event store so an added attendee is
 * reflected everywhere the event is used.
 */
public interface AddGuestDataAccessInterface {

    /**
     * This method retrieves an event by its id.
     * @param eventId the id of the event
     * @return the requested event
     * @throws WhoOwesWhatDataAccessException if the event does not exist
     */
    Event getEvent(int eventId) throws WhoOwesWhatDataAccessException;

    /**
     * This method stores or replaces an event (used to save the event with its new attendee).
     * @param event the event to store
     */
    void saveEvent(Event event);

}

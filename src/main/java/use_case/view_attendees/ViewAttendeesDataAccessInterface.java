package use_case.view_attendees;

import entity.Event;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/**
 * Event data required by the view-attendees use case.
 */
public interface ViewAttendeesDataAccessInterface {

    /**
     * Returns an event by identifier.
     *
     * @param eventId the event identifier
     * @return the event
     * @throws WhoOwesWhatDataAccessException when the event cannot be found
     */
    Event getEvent(int eventId) throws WhoOwesWhatDataAccessException;
}

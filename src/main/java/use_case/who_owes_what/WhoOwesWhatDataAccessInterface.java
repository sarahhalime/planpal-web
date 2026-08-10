package use_case.who_owes_what;

import entity.Event;

public interface WhoOwesWhatDataAccessInterface {

    /**
     * Gets an event by its ID.
     * @param eventId the ID of the event
     * @return the event
     * @throws WhoOwesWhatDataAccessException if the event does not exist
     */
    Event getEvent(int eventId) throws WhoOwesWhatDataAccessException;

}

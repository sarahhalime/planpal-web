package use_case.login;

import java.util.List;

import entity.Event;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

public interface LoginEventDataAccessInterface {

    /**
     * Gets a lost of event IDs the user is attending.
     * @param username the username of the user
     * @return a list of event IDs
     */
    List<Integer> getEventIds(String username);

    /**
     * Gets an event object by its ID.
     * @param eventId the ID of the event
     * @return the event object
     * @throws WhoOwesWhatDataAccessException if the event does not exist
     */
    Event getEvent(int eventId) throws WhoOwesWhatDataAccessException;
}

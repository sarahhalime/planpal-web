package use_case.load_user_events;

import java.util.List;

import entity.Event;

/**
 * The data-access interface for the load user events use case.
 */
public interface LoadUserEventsDataAccessInterface {

    /**
     * Loads all saved events belonging to the specified user.
     *
     * @param username the username whose events should be loaded
     * @return the user's saved events
     * @throws LoadUserEventsDataException if the saved event data cannot be loaded
     */
    List<Event> loadEvents(String username)
            throws LoadUserEventsDataException;
}

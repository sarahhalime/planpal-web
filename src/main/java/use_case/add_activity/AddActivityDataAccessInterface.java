package use_case.add_activity;

import entity.Event;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/**
 * Provides event persistence operations required by the add-activity use case.
 */
public interface AddActivityDataAccessInterface {

    /**
     * Retrieves an event by its identifier.
     *
     * @param eventId the event identifier
     * @return the matching event
     * @throws WhoOwesWhatDataAccessException if the event cannot be found
     */
    Event getEvent(int eventId) throws WhoOwesWhatDataAccessException;

    /**
     * Saves an event.
     *
     * @param event the event to save
     */
    void saveEvent(Event event);
}

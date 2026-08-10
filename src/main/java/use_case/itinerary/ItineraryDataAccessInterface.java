package use_case.itinerary;

import entity.Event;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/**
 * Provides event data needed to build an itinerary.
 */
public interface ItineraryDataAccessInterface {

    /**
     * Returns the requested event.
     *
     * @param eventId event identifier
     * @return event data
     * @throws WhoOwesWhatDataAccessException when the event cannot be loaded
     */
    Event getEvent(int eventId) throws WhoOwesWhatDataAccessException;
}

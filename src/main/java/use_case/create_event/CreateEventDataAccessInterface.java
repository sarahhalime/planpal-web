package use_case.create_event;

import entity.Event;

/**
 * The data-access interface for the create event use case.
 */
public interface CreateEventDataAccessInterface {
    /**
     * Returns the next available event ID.
     *
     * @return the next available event ID
     */
    int getNextEventId();

    /**
     * Stores the newly created event in the shared event store.
     *
     * @param event the event to store
     */
    void saveEvent(Event event);
}

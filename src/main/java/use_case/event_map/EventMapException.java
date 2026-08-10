package use_case.event_map;

/**
 * Signals that the event map could not be loaded.
 */
public final class EventMapException extends Exception {

    /**
     * Creates a map exception.
     *
     * @param message user-facing failure message
     */
    public EventMapException(String message) {
        super(message);
    }
}

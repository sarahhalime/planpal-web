package use_case.event_map;

/**
 * Input for loading the map of one event.
 */
public final class EventMapInputData {
    private final int eventId;

    /**
     * Creates map input data.
     *
     * @param eventId selected event identifier
     */
    public EventMapInputData(int eventId) {
        this.eventId = eventId;
    }

    /**
     * Returns the selected event identifier.
     *
     * @return event identifier
     */
    public int getEventId() {
        return this.eventId;
    }
}

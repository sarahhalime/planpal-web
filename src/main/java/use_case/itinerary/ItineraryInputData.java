package use_case.itinerary;

/**
 * Input data for loading an event itinerary.
 */
public final class ItineraryInputData {
    private final int eventId;

    /**
     * Creates an itinerary request.
     *
     * @param eventId event identifier
     */
    public ItineraryInputData(int eventId) {
        this.eventId = eventId;
    }

    /**
     * Returns the event identifier.
     *
     * @return event identifier
     */
    public int getEventId() {
        return this.eventId;
    }
}

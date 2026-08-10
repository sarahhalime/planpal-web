package use_case.view_attendees;

/**
 * Input data for viewing event attendees.
 */
public final class ViewAttendeesInputData {
    private final int eventId;

    /**
     * Creates input data for an event.
     *
     * @param eventId the event identifier
     */
    public ViewAttendeesInputData(int eventId) {
        this.eventId = eventId;
    }

    /**
     * Returns the event identifier.
     *
     * @return the event identifier
     */
    public int getEventId() {
        return this.eventId;
    }
}

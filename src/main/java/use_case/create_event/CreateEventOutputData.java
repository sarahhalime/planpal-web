package use_case.create_event;

/**
 * The output data for the create event use case.
 */
public class CreateEventOutputData {
    private final int eventId;
    private final String eventName;
    private final String username;
    private final String startDate;

    public CreateEventOutputData(int eventId, String eventName, String username, String startDate) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.username = username;
        this.startDate = startDate;
    }

    /**
     * Returns the ID of the newly created event.
     *
     * @return the event ID
     */
    public int getEventId() {
        return eventId;
    }

    /**
     * Returns the name of the newly created event.
     *
     * @return the event name
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Returns the username of the user who created the event.
     *
     * @return the creator's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the start date of the newly created event.
     *
     * @return the event start date
     */
    public String getStartDate() {
        return this.startDate;
    }
}

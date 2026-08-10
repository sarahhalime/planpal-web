package use_case.delete_event;

/**
 * The input data for the delete event use case.
 */
public class DeleteEventInputData {

    private final int eventId;
    private final String username;

    public DeleteEventInputData(int eventId, String username) {
        this.eventId = eventId;
        this.username = username;
    }

    public int getEventId() {
        return eventId;
    }

    public String getUsername() {
        return username;
    }
}

package use_case.delete_event;

/**
 * The output data for the delete event use case.
 */
public class DeleteEventOutputData {

    private final int eventId;
    private final String username;

    public DeleteEventOutputData(int eventId, String username) {
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

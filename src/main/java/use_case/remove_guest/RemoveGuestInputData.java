package use_case.remove_guest;

/**
 * The input data for the remove guest use case.
 * It identifies the event and the attendee (by id) to remove.
 */
public class RemoveGuestInputData {

    private final int eventId;
    private final String username;

    public RemoveGuestInputData(int eventId, String username) {
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

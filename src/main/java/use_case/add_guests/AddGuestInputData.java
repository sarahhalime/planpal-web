package use_case.add_guests;

public class AddGuestInputData {

    private final int eventId;
    private final String username;

    public AddGuestInputData(int eventId, String username) {
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

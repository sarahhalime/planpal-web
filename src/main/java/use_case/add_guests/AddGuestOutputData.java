package use_case.add_guests;

/**
 * The output data for the add guest use case.
 */
public class AddGuestOutputData {

    private final int eventId;
    private final String guestName;
    private final boolean useCaseFailed;

    public AddGuestOutputData(int eventId, String guestName, boolean useCaseFailed) {
        this.eventId = eventId;
        this.guestName = guestName;
        this.useCaseFailed = useCaseFailed;
    }

    public int getEventId() {
        return eventId;
    }

    public String getGuestName() {
        return guestName;
    }

    public boolean isUseCaseFailed() {
        return useCaseFailed;
    }
}

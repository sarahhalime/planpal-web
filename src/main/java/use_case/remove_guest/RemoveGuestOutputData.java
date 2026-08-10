package use_case.remove_guest;

/**
 * The output data for the remove guest use case.
 */
public class RemoveGuestOutputData {

    private final int eventId;
    private final String guestName;
    private final boolean useCaseFailed;

    public RemoveGuestOutputData(int eventId, String guestName, boolean useCaseFailed) {
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

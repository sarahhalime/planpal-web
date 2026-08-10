package use_case.who_owes_what;

public class WhoOwesWhatInputData {

    private final int eventId;

    public WhoOwesWhatInputData(int eventId) {
        this.eventId = eventId;
    }

    public int getEventId() {
        return eventId;
    }

}

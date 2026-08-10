package use_case.save_event;

/**
 * The input data for the save-event use case.
 */
public final class SaveEventInputData {

    private final String username;
    private final int eventId;
    private final String filePath;

    /**
     * Constructs the input data for saving an event.
     *
     * @param username the current username
     * @param eventId the selected event ID
     * @param filePath the destination file path
     */
    public SaveEventInputData(String username, int eventId, String filePath) {
        this.username = username;
        this.eventId = eventId;
        this.filePath = filePath;
    }

    public String getUsername() {
        return this.username;
    }

    public int getEventId() {
        return this.eventId;
    }

    public String getFilePath() {
        return this.filePath;
    }
}

package use_case.save_event;

/**
 * The output data for the save-event use case.
 */
public final class SaveEventOutputData {

    private final String eventName;

    /**
     * Constructs output data for a saved event.
     *
     * @param eventName the saved event name
     */
    public SaveEventOutputData(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return this.eventName;
    }
}

package use_case.remove_activity;

/**
 * Contains the information required to remove an activity.
 */
public final class RemoveActivityInputData {

    private final int eventId;
    private final int activityIndex;

    /**
     * Creates remove-activity input data.
     *
     * @param eventId the event identifier
     * @param activityIndex the activity list index
     */
    public RemoveActivityInputData(int eventId, int activityIndex) {
        this.eventId = eventId;
        this.activityIndex = activityIndex;
    }

    /**
     * Get the event id.
     * @return the event id
     */
    public int getEventId() {
        return this.eventId;
    }

    /**
     * The activity index.
     * @return activity index
     */
    public int getActivityIndex() {
        return this.activityIndex;
    }
}

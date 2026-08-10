package use_case.remove_activity;

/**
 * Contains the result of successfully removing an activity.
 */
public final class RemoveActivityOutputData {

    private final int activityIndex;
    private final String activityName;

    /**
     * Creates remove-activity output data.
     *
     * @param activityIndex the removed activity list index
     * @param activityName the removed activity name
     */
    public RemoveActivityOutputData(int activityIndex, String activityName) {
        this.activityIndex = activityIndex;
        this.activityName = activityName;
    }

    /**
     * The activity index.
     * @return activity index
     */
    public int getActivityIndex() {
        return this.activityIndex;
    }

    /**
     * The activity name.
     * @return activity name
     */
    public String getActivityName() {
        return this.activityName;
    }
}

package use_case.edit_activity;

/**
 * Contains the result of successfully editing an activity.
 */
public final class EditActivityOutputData {

    private final int activityIndex;
    private final String activityName;
    private final String date;
    private final String time;
    private final String location;

    /**
     * Creates edit-activity output data.
     *
     * @param activityIndex the edited activity list index
     * @param activityName the updated activity name
     * @param date the updated date
     * @param time the updated time
     * @param location the updated location
     */
    public EditActivityOutputData(
            int activityIndex,
            String activityName,
            String date,
            String time,
            String location) {

        this.activityIndex = activityIndex;
        this.activityName = activityName;
        this.date = date;
        this.time = time;
        this.location = location;
    }

    /**
     * Gets activity index.
     * @return activity index
     */
    public int getActivityIndex() {
        return this.activityIndex;
    }

    /**
     * The activity name.
     * @return the activity name.
     */
    public String getActivityName() {
        return this.activityName;
    }

    /**
     * Get the date.
     * @return the date.
     */
    public String getDate() {
        return this.date;
    }

    /**
     * Get the time.
     * @return the time
     */
    public String getTime() {
        return this.time;
    }

    /**
     * The location.
     * @return the location
     */
    public String getLocation() {
        return this.location;
    }
}

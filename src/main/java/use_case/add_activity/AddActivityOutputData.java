package use_case.add_activity;

/**
 * Contains the result of successfully adding an activity.
 */
public final class AddActivityOutputData {

    private final String activityName;
    private final String date;
    private final String time;
    private final String location;

    /**
     * Creates add-activity output data.
     *
     * @param activityName the activity name
     * @param date the activity date
     * @param time the activity time
     * @param location the activity location
     */
    public AddActivityOutputData(
            String activityName,
            String date,
            String time,
            String location) {

        this.activityName = activityName;
        this.date = date;
        this.time = time;
        this.location = location;
    }

    /**
     * Returns the activity name.
     *
     * @return the activity name
     */
    public String getActivityName() {
        return this.activityName;
    }

    /**
     * Returns the activity date.
     *
     * @return the activity date
     */
    public String getDate() {
        return this.date;
    }

    /**
     * Returns the activity time.
     *
     * @return the activity time
     */
    public String getTime() {
        return this.time;
    }

    /**
     * Returns the activity location.
     *
     * @return the activity location
     */
    public String getLocation() {
        return this.location;
    }
}

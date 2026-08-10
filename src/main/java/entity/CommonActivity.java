package entity;

/**
 * Represents the default implementation of an event activity.
 */
public class CommonActivity implements Activity {

    private final String activityName;
    private final String date;
    private final String time;
    private final String location;

    /**
     * Creates an activity.
     *
     * @param activityName the activity name
     * @param date the activity date
     * @param time the activity time
     * @param location the activity location
     */
    public CommonActivity(String activityName, String date, String time, String location) {
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
    @Override
    public String getActivityName() {
        return this.activityName;
    }

    /**
     * Returns the activity date.
     *
     * @return the activity date
     */
    @Override
    public String getDate() {
        return this.date;
    }

    /**
     * Returns the activity time.
     *
     * @return the activity time
     */
    @Override
    public String getTime() {
        return this.time;
    }

    /**
     * Returns the activity location.
     *
     * @return the activity location
     */
    @Override
    public String getLocation() {
        return this.location;
    }
}

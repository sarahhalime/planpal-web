package use_case.select_event;

/**
 * Contains activity information returned when an event is selected.
 */
public final class SelectEventActivityData {

    private final String activityName;
    private final String date;
    private final String time;
    private final String location;

    /**
     * Creates selected-event activity data.
     *
     * @param activityName the activity name
     * @param date the activity date
     * @param time the activity time
     * @param location the activity location
     */
    public SelectEventActivityData(
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

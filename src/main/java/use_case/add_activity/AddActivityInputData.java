package use_case.add_activity;

/**
 * Contains the information required to add an activity to an event.
 */
public final class AddActivityInputData {

    private final int eventId;
    private final String activityName;
    private final String date;
    private final String time;
    private final String location;

    /**
     * Creates add-activity input data.
     *
     * @param eventId the event identifier
     * @param activityName the activity name
     * @param date the activity date
     * @param time the activity time
     * @param location the activity location
     */
    public AddActivityInputData(
            int eventId,
            String activityName,
            String date,
            String time,
            String location) {

        this.eventId = eventId;
        this.activityName = activityName;
        this.date = date;
        this.time = time;
        this.location = location;
    }

    /**
     * Returns the event identifier.
     *
     * @return the event identifier
     */
    public int getEventId() {
        return this.eventId;
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

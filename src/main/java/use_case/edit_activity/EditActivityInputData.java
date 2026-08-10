package use_case.edit_activity;

/**
 * Contains the information required to edit an activity.
 */
public final class EditActivityInputData {

    private final int eventId;
    private final int activityIndex;
    private final String activityName;
    private final String date;
    private final String time;
    private final String location;

    /**
     * Creates edit-activity input data.
     *
     * @param eventId the event identifier
     * @param activityIndex the activity list index
     * @param activityName the updated activity name
     * @param date the updated activity date
     * @param time the updated activity time
     * @param location the updated activity location
     */
    public EditActivityInputData(
            int eventId,
            int activityIndex,
            String activityName,
            String date,
            String time,
            String location) {

        this.eventId = eventId;
        this.activityIndex = activityIndex;
        this.activityName = activityName;
        this.date = date;
        this.time = time;
        this.location = location;
    }

    /**
     * Returns the event id.
     * @return the event identifier
     */
    public int getEventId() {
        return this.eventId;
    }

    /**
     * Returns the activity list index.
     * @return the activity list index
     */
    public int getActivityIndex() {
        return this.activityIndex;
    }

    /**
     * Returns the activity name.
     * @return the updated activity name
     */
    public String getActivityName() {
        return this.activityName;
    }

    /**
     * The updated date.
     * @return The updated date.
     */
    public String getDate() {
        return this.date;
    }

    /**
     * The updated time.
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

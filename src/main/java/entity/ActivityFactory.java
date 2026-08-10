package entity;

/**
 * Creates activities without coupling the use-case layer to a concrete activity implementation.
 */
public interface ActivityFactory {

    /**
     * Creates an activity.
     *
     * @param activityName the activity name
     * @param date the activity date
     * @param time the activity time
     * @param location the activity location
     * @return the created activity
     */
    Activity create(String activityName, String date, String time, String location);
}

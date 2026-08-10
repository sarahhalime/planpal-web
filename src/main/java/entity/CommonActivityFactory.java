package entity;

/**
 * Creates default activity entities.
 */
public class CommonActivityFactory implements ActivityFactory {

    /**
     * Creates a default activity.
     *
     * @param activityName the activity name
     * @param date the activity date
     * @param time the activity time
     * @param location the activity location
     * @return the created activity
     */
    @Override
    public Activity create(String activityName, String date, String time, String location) {
        return new CommonActivity(activityName, date, time, location);
    }
}

package entity;

/**
 * An activity scheduled as part of an event itinerary.
 */
public interface Activity {

    /**
     * Returns the activity name.
     * @return the activity name
     */
    String getActivityName();

    /**
     * Returns the activity date.
     * @return the activity date
     */
    String getDate();

    /**
     * Returns the activity time.
     * @return the activity time
     */
    String getTime();

    /**
     * Returns the activity location.
     * @return the activity location
     */
    String getLocation();
}

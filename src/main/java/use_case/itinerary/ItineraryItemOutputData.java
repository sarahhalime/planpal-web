package use_case.itinerary;

/**
 * One activity prepared for chronological itinerary display.
 */
public final class ItineraryItemOutputData {
    private final String activityName;
    private final String date;
    private final String time;
    private final String location;
    private final ItineraryTravelOutputData travelToNext;

    /**
     * Creates an itinerary activity without a following travel leg.
     *
     * @param activityName activity name
     * @param date activity date
     * @param time activity time
     * @param location activity location
     */
    public ItineraryItemOutputData(
            String activityName,
            String date,
            String time,
            String location) {
        this(activityName, date, time, location, null);
    }

    /**
     * Creates an itinerary activity.
     *
     * @param activityName activity name
     * @param date activity date
     * @param time activity time
     * @param location activity location
     * @param travelToNext travel information to the next activity
     */
    public ItineraryItemOutputData(
            String activityName,
            String date,
            String time,
            String location,
            ItineraryTravelOutputData travelToNext) {
        this.activityName = activityName;
        this.date = date;
        this.time = time;
        this.location = location;
        this.travelToNext = travelToNext;
    }

    /**
     * Returns the activity name.
     *
     * @return activity name
     */
    public String getActivityName() {
        return this.activityName;
    }

    /**
     * Returns the activity date.
     *
     * @return activity date
     */
    public String getDate() {
        return this.date;
    }

    /**
     * Returns the activity time.
     *
     * @return activity time
     */
    public String getTime() {
        return this.time;
    }

    /**
     * Returns the activity location.
     *
     * @return activity location
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * Returns travel information to the next itinerary activity.
     *
     * @return travel data, or null when no leg should be displayed
     */
    public ItineraryTravelOutputData getTravelToNext() {
        return this.travelToNext;
    }
}

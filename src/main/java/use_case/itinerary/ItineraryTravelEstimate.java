package use_case.itinerary;

/**
 * Travel-time estimates between two itinerary locations.
 */
public final class ItineraryTravelEstimate {
    public static final int UNAVAILABLE_MINUTES = -1;

    private final int drivingMinutes;
    private final int walkingMinutes;

    /**
     * Creates a travel-time estimate.
     *
     * @param drivingMinutes driving time in minutes, or {@link #UNAVAILABLE_MINUTES}
     * @param walkingMinutes walking time in minutes, or {@link #UNAVAILABLE_MINUTES}
     */
    public ItineraryTravelEstimate(int drivingMinutes, int walkingMinutes) {
        this.drivingMinutes = drivingMinutes;
        this.walkingMinutes = walkingMinutes;
    }

    /**
     * Returns the driving time.
     *
     * @return driving time in minutes, or {@link #UNAVAILABLE_MINUTES}
     */
    public int getDrivingMinutes() {
        return this.drivingMinutes;
    }

    /**
     * Returns the walking time.
     *
     * @return walking time in minutes, or {@link #UNAVAILABLE_MINUTES}
     */
    public int getWalkingMinutes() {
        return this.walkingMinutes;
    }

    /**
     * Returns whether a driving estimate is available.
     *
     * @return true when driving time is available
     */
    public boolean hasDrivingTime() {
        return this.drivingMinutes >= 0;
    }

    /**
     * Returns whether a walking estimate is available.
     *
     * @return true when walking time is available
     */
    public boolean hasWalkingTime() {
        return this.walkingMinutes >= 0;
    }

    /**
     * Returns whether at least one travel mode is available.
     *
     * @return true when a route estimate exists
     */
    public boolean hasAnyTime() {
        return this.hasDrivingTime() || this.hasWalkingTime();
    }
}

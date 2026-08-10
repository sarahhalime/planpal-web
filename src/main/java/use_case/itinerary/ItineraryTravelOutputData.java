package use_case.itinerary;

/**
 * Travel information displayed between two itinerary activities.
 */
public final class ItineraryTravelOutputData {
    private final int drivingMinutes;
    private final int walkingMinutes;
    private final int availableMinutes;
    private final int bufferMinutes;
    private final ItineraryTravelStatus status;
    private final ItineraryTravelFailure failure;

    /**
     * Creates itinerary travel output with no routing failure.
     *
     * @param drivingMinutes driving time, or {@link ItineraryTravelEstimate#UNAVAILABLE_MINUTES}
     * @param walkingMinutes walking time, or {@link ItineraryTravelEstimate#UNAVAILABLE_MINUTES}
     * @param availableMinutes schedule gap, or {@link ItineraryTravelEstimate#UNAVAILABLE_MINUTES}
     * @param bufferMinutes time remaining after travel, or unavailable
     * @param status schedule status
     */
    public ItineraryTravelOutputData(
            int drivingMinutes,
            int walkingMinutes,
            int availableMinutes,
            int bufferMinutes,
            ItineraryTravelStatus status) {
        this(
                drivingMinutes,
                walkingMinutes,
                availableMinutes,
                bufferMinutes,
                status,
                ItineraryTravelFailure.NONE
        );
    }

    /**
     * Creates itinerary travel output.
     *
     * @param drivingMinutes driving time, or unavailable
     * @param walkingMinutes walking time, or unavailable
     * @param availableMinutes schedule gap, or unavailable
     * @param bufferMinutes time remaining after travel, or unavailable
     * @param status schedule status
     * @param failure routing failure category
     */
    public ItineraryTravelOutputData(
            int drivingMinutes,
            int walkingMinutes,
            int availableMinutes,
            int bufferMinutes,
            ItineraryTravelStatus status,
            ItineraryTravelFailure failure) {
        this.drivingMinutes = drivingMinutes;
        this.walkingMinutes = walkingMinutes;
        this.availableMinutes = availableMinutes;
        this.bufferMinutes = bufferMinutes;
        this.status = status;
        this.failure = failure;
    }

    /**
     * Returns driving time.
     *
     * @return minutes, or unavailable
     */
    public int getDrivingMinutes() {
        return this.drivingMinutes;
    }

    /**
     * Returns walking time.
     *
     * @return minutes, or unavailable
     */
    public int getWalkingMinutes() {
        return this.walkingMinutes;
    }

    /**
     * Returns the time between activity start times.
     *
     * @return available minutes, or unavailable
     */
    public int getAvailableMinutes() {
        return this.availableMinutes;
    }

    /**
     * Returns the schedule buffer after travel.
     *
     * @return buffer minutes, or unavailable
     */
    public int getBufferMinutes() {
        return this.bufferMinutes;
    }

    /**
     * Returns the schedule status.
     *
     * @return travel schedule status
     */
    public ItineraryTravelStatus getStatus() {
        return this.status;
    }

    /**
     * Returns why routing failed.
     *
     * @return routing failure category
     */
    public ItineraryTravelFailure getFailure() {
        return this.failure;
    }
}

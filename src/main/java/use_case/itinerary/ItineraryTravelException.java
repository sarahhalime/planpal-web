package use_case.itinerary;

/**
 * Indicates that travel-time information could not be retrieved.
 */
public final class ItineraryTravelException extends Exception {
    private final ItineraryTravelFailure failure;

    /**
     * Creates a travel-time exception with a generic service failure.
     *
     * @param message user-facing failure message
     */
    public ItineraryTravelException(String message) {
        this(message, ItineraryTravelFailure.SERVICE_UNAVAILABLE);
    }

    /**
     * Creates a travel-time exception.
     *
     * @param message user-facing failure message
     * @param failure failure category
     */
    public ItineraryTravelException(
            String message,
            ItineraryTravelFailure failure) {
        super(message);
        this.failure = failure;
    }

    /**
     * Returns the failure category.
     *
     * @return failure category
     */
    public ItineraryTravelFailure getFailure() {
        return this.failure;
    }
}

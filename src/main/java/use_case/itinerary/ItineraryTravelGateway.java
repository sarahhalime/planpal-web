package use_case.itinerary;

/**
 * External routing service used to estimate travel between itinerary activities.
 */
public interface ItineraryTravelGateway {

    /**
     * Estimates driving and walking time between two saved locations.
     *
     * @param origin origin location text
     * @param destination destination location text
     * @return available travel-time estimates
     * @throws ItineraryTravelException when routing cannot be completed
     */
    ItineraryTravelEstimate estimateTravel(String origin, String destination)
            throws ItineraryTravelException;
}

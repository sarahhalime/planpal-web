package use_case.itinerary;

/**
 * Explains why itinerary travel information could not be produced.
 */
public enum ItineraryTravelFailure {
    NONE,
    LOCATION_NOT_RESOLVED,
    NO_ROUTE,
    SERVICE_UNAVAILABLE
}

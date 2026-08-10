package use_case.itinerary;

/**
 * Input boundary for loading an event itinerary.
 */
public interface ItineraryInputBoundary {

    /**
     * Loads the itinerary for an event.
     *
     * @param inputData itinerary request
     */
    void execute(ItineraryInputData inputData);
}

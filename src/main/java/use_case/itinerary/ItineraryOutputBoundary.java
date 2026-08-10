package use_case.itinerary;

/**
 * Output boundary for the itinerary feature.
 */
public interface ItineraryOutputBoundary {

    /**
     * Presents a successfully loaded itinerary.
     *
     * @param outputData ordered itinerary data
     */
    void prepareSuccessView(ItineraryOutputData outputData);

    /**
     * Presents an itinerary loading failure.
     *
     * @param errorMessage failure message
     */
    void prepareFailView(String errorMessage);
}

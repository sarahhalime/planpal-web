package use_case.event_map;

/**
 * Output boundary for the event-map use case.
 */
public interface EventMapOutputBoundary {

    /**
     * Presents a rendered event map.
     *
     * @param outputData rendered map data
     */
    void prepareSuccessView(EventMapOutputData outputData);

    /**
     * Presents a map-loading failure.
     *
     * @param errorMessage user-facing error message
     */
    void prepareFailView(String errorMessage);
}

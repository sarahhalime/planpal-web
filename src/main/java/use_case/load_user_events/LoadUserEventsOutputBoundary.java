package use_case.load_user_events;

/**
 * The output boundary for the load user events use case.
 */
public interface LoadUserEventsOutputBoundary {

    /**
     * Prepares the successfully loaded events for presentation.
     *
     * @param loadUserEventsOutputData the loaded event information
     */
    void prepareSuccessView(
            LoadUserEventsOutputData loadUserEventsOutputData);

    /**
     * Prepares an event-loading failure for presentation.
     *
     * @param errorMessage the explanation of why loading failed
     */
    void prepareFailView(String errorMessage);
}

package use_case.save_event;

/**
 * The output boundary for the save event use case.
 */
public interface SaveEventOutputBoundary {

    /**
     * Prepares the successful save result for presentation.
     * @param saveEventOutputData information about the saved event
     */
    void prepareSuccessView(SaveEventOutputData saveEventOutputData);

    /**
     * Prepares a save failure for presentation.
     * @param errorMessage the explanation of why saving failed
     */
    void prepareFailView(String errorMessage);
}

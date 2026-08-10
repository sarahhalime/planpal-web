package use_case.edit_event;

/**
 * The output boundary for the edit event use case.
 */
public interface EditEventOutputBoundary {
    /**
     * Prepares the success view for the edit event use case.
     *
     * @param outputData the output data
     */
    void prepareSuccessView(EditEventOutputData outputData);

    /**
     * Prepares the failure view for the edit event use case.
     *
     * @param errorMessage the error message
     */
    void prepareFailView(String errorMessage);
}

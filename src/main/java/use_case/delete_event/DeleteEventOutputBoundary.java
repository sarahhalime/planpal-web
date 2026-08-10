package use_case.delete_event;

/**
 * The output boundary for the delete event use case.
 */
public interface DeleteEventOutputBoundary {

    /**
     * Prepares the success view for the delete event use case.
     *
     * @param outputData the output data
     */
    void prepareSuccessView(DeleteEventOutputData outputData);

    /**
     * Prepares the failure view for the delete event use case.
     *
     * @param errorMessage the error message
     */
    void prepareFailView(String errorMessage);
}

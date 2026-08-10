package use_case.remove_guest;

/**
 * The output boundary for the remove guest use case.
 */
public interface RemoveGuestOutputBoundary {

    /**
     * This method prepares the success view for the rmove guest use case.
     * @param outputData the output data
     */
    void prepareSuccessView(RemoveGuestOutputData outputData);

    /**
     * This method prepares the failure view for the remove guest use case.
     * @param errorMessage the error message of the failure
     */
    void prepareFailView(String errorMessage);
}

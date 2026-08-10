package use_case.change_username;

public interface ChangeUsernameOutputBoundary {

    /**
     * Prepares the success view for the Change Username Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(ChangeUsernameOutputData outputData);

    /**
     * Prepares the failure view for the Change Username Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailureView(String errorMessage);
}

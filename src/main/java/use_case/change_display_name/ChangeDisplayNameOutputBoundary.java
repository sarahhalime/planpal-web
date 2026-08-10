package use_case.change_display_name;

public interface ChangeDisplayNameOutputBoundary {

    /**
     * Prepares the success view for the Change Display Name Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(ChangeDisplayNameOutputData outputData);

    /**
     * Prepares the failure view for the Change Display Name Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailureView(String errorMessage);
}

package use_case.signup;

/**
 * The output boundary for the signup use case.
 */
public interface SignupOutputBoundary {

    /**
     * Prepare success view for the signup use case.
     * @param outputData the output data
     */
    void prepareSuccessView(SignupOutputData outputData);

    /**
     * Prepare failure view for the signup use case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);

    /**
     * To switch to the login view.
     */
    void switchToLoginView();
}

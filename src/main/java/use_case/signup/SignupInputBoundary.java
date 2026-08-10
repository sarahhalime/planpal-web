package use_case.signup;

/**
 * The input boundary for actions which are related to signing up.
 */
public interface SignupInputBoundary {

    /**
     * To execute the signup use case.
     * @param signupInputData the input data
     */
    void execute(SignupInputData signupInputData);

    /**
     * To execute the switch to login view use case.
     */
    void switchToLoginView();
}

package use_case.login;

public interface LoginInputBoundary {

    /**
     * Executes the login use case.
     * @param inputData The input data for the login use case.
     */
    void execute(LoginInputData inputData);
}

package use_case.change_username;

/**
 * The change username use case.
 */
public interface ChangeUsernameInputBoundary {
    /**
     * Execute the Change Username Use Case.
     * @param changeUsernameInputData the input data for this use case
     */
    void execute(ChangeUsernameInputData changeUsernameInputData);
}

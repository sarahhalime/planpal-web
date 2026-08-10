package use_case.change_display_name;

/**
 * The change display name use case.
 */
public interface ChangeDisplayNameInputBoundary {

    /**
     * Execute the Change Display Name Use Case.
     * @param changeDisplayNameInputData the input data for this use case
     */
    void execute(ChangeDisplayNameInputData changeDisplayNameInputData);
}

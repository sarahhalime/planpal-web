package use_case.remove_expense;

/**
 * The output boundary for the remove expense use case.
 */
public interface RemoveExpenseOutputBoundary {

    /**
     * This method prepares the success view for the remove expense use case.
     * @param outputData the output data
     */
    void prepareSuccessView(RemoveExpenseOutputData outputData);

    /**
     * This method prepares the failure view for the remove expense use case.
     * @param errorMessage the error message of the failure
     */
    void prepareFailView(String errorMessage);
}

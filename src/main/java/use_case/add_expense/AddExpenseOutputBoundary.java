package use_case.add_expense;

/**
 * The output boundary for the add expense use case.
 */
public interface AddExpenseOutputBoundary {

    /**
     * This method prepares the success view for the add expense use case.
     * @param outputData the output data
     */
    void prepareSuccessView(AddExpenseOutputData outputData);

    /**
     * This method prepares the failure view for the add expense use case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}
    

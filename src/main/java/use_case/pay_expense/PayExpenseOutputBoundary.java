package use_case.pay_expense;

/**
 * Output boundary for toggling an expense's settlement status.
 */
public interface PayExpenseOutputBoundary {

    /**
     * Prepares the success view.
     * @param outputData The output data.
     */
    void prepareSuccessView(PayExpenseOutputData outputData);

    /**
     * Shows an error message.
     * @param errorMessage The error message.
     */
    void prepareFailView(String errorMessage);
}

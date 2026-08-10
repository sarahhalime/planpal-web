package use_case.edit_expense;

/**
 * The output boundary for the edit expense use case.
 */
public interface EditExpenseOutputBoundary {

    /**
     * Prepares the success view for the edit expense use case.
     *
     * @param outputData the output data
     */
    void prepareSuccessView(EditExpenseOutputData outputData);

    /**
     * Prepares the error view for the edit expense use case.
     *
     * @param errorMessage the explanation of the error
     */
    void prepareFailView(String errorMessage);

    /**
     * Prefills the edit form with the existing expense values.
     *
     * @param prefillData existing expense values
     */
    default void preparePrefillView(EditExpensePrefillData prefillData) {
        // Optional for tests and presenters that do not own the edit form.
    }

    /**
     * Sets the expense id for the edit expense use case.
     *
     * @param expenseId the expense id
     */
    default void setUpId(int expenseId) {
        // Kept for backwards compatibility with older presenters/tests.
    }

    /**
     * Sets the current payment status for the expense being edited.
     *
     * @param expenseStatus the current payment status
     */
    default void setUpStatus(String expenseStatus) {
        // Optional hook for presenters that maintain the selected expense status.
    }
}

package use_case.edit_expense;

/**
 * Input Boundary for actions which are related to editing an expense.
 */
public interface EditExpenseInputBoundary {

    /**
     * Executes the edit expense use case.
     *
     * @param editExpenseInputData the input data
     */
    void execute(EditExpenseInputData editExpenseInputData);

    /**
     * Loads the current values of the expense being edited.
     *
     * @param eventId selected event identifier
     * @param expenseId expense identifier
     */
    void setUpFields(int eventId, int expenseId);
}

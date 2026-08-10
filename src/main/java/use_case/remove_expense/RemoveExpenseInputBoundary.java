package use_case.remove_expense;

/**
 * The input boundary for actions which are related to removing an expense.
 */
public interface RemoveExpenseInputBoundary {

    /**
     * This method executes the remove expense use case.
     * @param removeExpenseInputData the input data
     */
    void execute(RemoveExpenseInputData removeExpenseInputData);
}

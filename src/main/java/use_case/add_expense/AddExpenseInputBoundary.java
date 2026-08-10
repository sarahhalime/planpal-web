package use_case.add_expense;

/**
 * The input Boundary for actions which are related to adding an expense.
 */
public interface AddExpenseInputBoundary {

    /**
     * To execute the add expense use case.
     * @param addExpenseInputData the input data
     */
    void execute(AddExpenseInputData addExpenseInputData);
}

package use_case.pay_expense;

public interface PayExpenseInputBoundary {

    /**
     * Pays an expense.
     * @param payExpenseInputData the input data
     */
    void execute(PayExpenseInputData payExpenseInputData);
}

package entity;

import java.util.Map;
import java.util.Set;

public class CommonExpenseFactory implements ExpenseFactory {

    @Override
    public Expense create(int expenseId, String expenseName, String payerUsername, double totalAmount,
                          boolean isCustomSplit, Set<String> debtors, Map<String, Double> expenseSplit) {
        return new CommonExpense(expenseId, expenseName, payerUsername, totalAmount,
                isCustomSplit, debtors, expenseSplit);
    }
}

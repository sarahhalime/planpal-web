package entity;

import java.util.Map;
import java.util.Set;

public interface ExpenseFactory {

    /**
     * Creates a new Expense.
     * @param expenseId the id of the expense
     * @param expenseName the name of the expense
     * @param payerUsername the username of the attendee who paid
     * @param totalAmount the total amount of the expense
     * @param isCustomSplit whether the expense is split equally or custom
     * @param debtors the usernames in debt to the payer (equal split)
     * @param expenseSplit a mapping of usernames to amount owed (custom split)
     * @return the new expense
     */
    Expense create(int expenseId, String expenseName, String payerUsername, double totalAmount,
                   boolean isCustomSplit, Set<String> debtors, Map<String, Double> expenseSplit);
}

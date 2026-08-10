package use_case.edit_expense;

import java.util.Map;
import java.util.Set;

/**
 * Contains the existing values of an expense being edited.
 */
public final class EditExpensePrefillData {

    private static final String DEFAULT_CURRENCY = "CAD";

    private final int expenseId;
    private final String expenseName;
    private final double amount;
    private final String currencyCode;
    private final String payerUsername;
    private final boolean customSplit;
    private final Set<String> debtors;
    private final Map<String, Double> expenseSplits;
    private final String status;

    /**
     * Creates edit-expense prefill data in the default currency.
     *
     * @param expenseId expense identifier
     * @param expenseName existing expense name
     * @param amount existing total amount
     * @param payerUsername existing payer username
     * @param split how the expense is divided
     * @param status existing settlement status
     */
    public EditExpensePrefillData(
            int expenseId,
            String expenseName,
            double amount,
            String payerUsername,
            ExpenseSplitDetails split,
            String status
    ) {
        this(expenseId, expenseName, amount, DEFAULT_CURRENCY, payerUsername, split, status);
    }

    /**
     * Creates edit-expense prefill data.
     *
     * @param expenseId expense identifier
     * @param expenseName existing expense name
     * @param amount existing total amount
     * @param currencyCode existing input currency
     * @param payerUsername existing payer username
     * @param split how the expense is divided
     * @param status existing settlement status
     */
    public EditExpensePrefillData(
            int expenseId,
            String expenseName,
            double amount,
            String currencyCode,
            String payerUsername,
            ExpenseSplitDetails split,
            String status
    ) {
        this.expenseId = expenseId;
        this.expenseName = expenseName;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.payerUsername = payerUsername;
        this.customSplit = split.isCustomSplit();
        this.debtors = split.getDebtors();
        this.expenseSplits = split.getExpenseSplits();
        this.status = status;
    }

    public int getExpenseId() {
        return this.expenseId;
    }

    public String getExpenseName() {
        return this.expenseName;
    }

    public double getAmount() {
        return this.amount;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public String getPayerUsername() {
        return this.payerUsername;
    }

    public boolean isCustomSplit() {
        return this.customSplit;
    }

    public Set<String> getDebtors() {
        return this.debtors;
    }

    public Map<String, Double> getExpenseSplits() {
        return this.expenseSplits;
    }

    public String getStatus() {
        return this.status;
    }
}

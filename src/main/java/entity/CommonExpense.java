package entity;

import java.util.Map;
import java.util.Set;

public class CommonExpense implements Expense {

    private final int expenseId;
    private final String expenseName;
    private final String payerUsername;
    private final double totalAmount;
    private double originalAmount;
    private String originalCurrency;
    private final boolean isCustomSplit;
    private final Set<String> debtors;
    private final Map<String, Double> expenseSplits;
    private Map<String, Double> originalExpenseSplits;
    private String status;

    public CommonExpense(int expenseId, String expenseName, String payerUsername, double totalAmount,
                         boolean isCustomSplit, Set<String> debtors, Map<String, Double> expenseSplits) {
        this.expenseId = expenseId;
        this.expenseName = expenseName;
        this.payerUsername = payerUsername;
        this.totalAmount = totalAmount;
        this.isCustomSplit = isCustomSplit;
        this.debtors = debtors;
        this.expenseSplits = expenseSplits;
        this.originalAmount = totalAmount;
        this.originalCurrency = "CAD";
        this.originalExpenseSplits = expenseSplits;
        this.status = "UNPAID";
    }

    @Override
    public int getExpenseId() {
        return expenseId;
    }

    @Override
    public String getExpenseName() {
        return expenseName;
    }

    @Override
    public String getPayerUsername() {
        return payerUsername;
    }

    @Override
    public double getTotalAmount() {
        return totalAmount;
    }

    @Override
    public double getOriginalAmount() {
        return originalAmount;
    }

    @Override
    public String getOriginalCurrency() {
        return originalCurrency;
    }

    @Override
    public boolean isCustomSplit() {
        return isCustomSplit;
    }

    @Override
    public Set<String> getDebtors() {
        return debtors;
    }

    @Override
    public Map<String, Double> getExpenseSplits() {
        return expenseSplits;
    }

    @Override
    public Map<String, Double> getOriginalExpenseSplits() {
        return originalExpenseSplits;
    }

    @Override
    public void setOriginalValues(
            double amount,
            String currency,
            Map<String, Double> splits) {

        this.originalAmount = amount;
        this.originalCurrency = currency;
        this.originalExpenseSplits = splits;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatusPaid() {
        this.status = "PAID";
    }

    @Override
    public void setStatusUnpaid() {
        this.status = "UNPAID";
    }
}

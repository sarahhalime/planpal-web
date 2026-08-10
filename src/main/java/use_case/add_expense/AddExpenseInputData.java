package use_case.add_expense;

import java.util.Map;
import java.util.Set;

public class AddExpenseInputData {

    private final int eventId;
    private final String expenseName;
    private final String payerUsername;
    private final double totalAmount;
    private String expenseCurrency;
    private final boolean isCustomSplit;
    private final Set<String> debtors;
    private final Map<String, Double> customSplit;

    public AddExpenseInputData(int eventId, String expenseName, String payerUsername,
                               double totalAmount, boolean isCustomSplit,
                               Set<String> debtors, Map<String, Double> customSplit) {
        this.eventId = eventId;
        this.expenseName = expenseName;
        this.payerUsername = payerUsername;
        this.totalAmount = totalAmount;
        this.expenseCurrency = "CAD";
        this.isCustomSplit = isCustomSplit;
        this.debtors = debtors;
        this.customSplit = customSplit;
    }

    public int getEventId() {
        return eventId;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public String getPayerUsername() {
        return payerUsername;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getExpenseCurrency() {
        return expenseCurrency;
    }

    public void setExpenseCurrency(String expenseCurrency) {
        this.expenseCurrency = expenseCurrency;
    }

    public boolean isCustomSplit() {
        return isCustomSplit;
    }

    public Set<String> getDebtors() {
        return debtors;
    }

    public Map<String, Double> getCustomSplit() {
        return customSplit;
    }
}

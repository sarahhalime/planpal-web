package use_case.edit_expense;

import java.util.Map;
import java.util.Set;

import entity.ExpensesData;

public class EditExpenseInputData {

    private final int eventId;
    private final ExpensesData expensesData;
    private final String expenseCurrency;
    private final boolean isCustomSplit;
    private final Set<String> debtors;
    private final Map<String, Double> customSplit;

    public EditExpenseInputData(int eventId, ExpensesData expensesData, String expenseCurrency, boolean isCustomSplit,
                                Set<String> debtors, Map<String, Double> customSplit) {
        this.eventId = eventId;
        this.expensesData = expensesData;
        this.expenseCurrency = expenseCurrency;
        this.isCustomSplit = isCustomSplit;
        this.debtors = debtors;
        this.customSplit = customSplit;
    }

    public EditExpenseInputData(int eventId, ExpensesData expensesData, boolean isCustomSplit,
                                Set<String> debtors, Map<String, Double> customSplit) {
        this(eventId, expensesData, expensesData.getCurrencyCode(),
                isCustomSplit, debtors, customSplit);
    }

    public int getEventId() {
        return this.eventId;
    }

    public ExpensesData getExpensesData() {
        return this.expensesData;
    }

    public String getExpenseCurrency() {
        return this.expenseCurrency;
    }

    public boolean isCustomSplit() {
        return this.isCustomSplit;
    }

    public Set<String> getDebtors() {
        return this.debtors;
    }

    public Map<String, Double> getCustomSplit() {
        return this.customSplit;
    }
}

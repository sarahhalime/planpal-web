package use_case.edit_expense;

import java.util.Map;
import java.util.Set;

/**
 * How an expense is divided between the people who owe for it.
 */
public final class ExpenseSplitDetails {

    private final boolean customSplit;
    private final Set<String> debtors;
    private final Map<String, Double> expenseSplits;

    /**
     * Creates the split details for an expense.
     *
     * @param customSplit whether the amounts were entered by hand
     * @param debtors the people the expense is split between
     * @param expenseSplits the username-to-amount mapping
     */
    public ExpenseSplitDetails(boolean customSplit, Set<String> debtors,
                               Map<String, Double> expenseSplits) {
        this.customSplit = customSplit;
        this.debtors = Set.copyOf(debtors);
        this.expenseSplits = Map.copyOf(expenseSplits);
    }

    /**
     * Whether the amounts were entered by hand.
     * @return true when the split is custom
     */
    public boolean isCustomSplit() {
        return this.customSplit;
    }

    /**
     * The people the expense is split between.
     * @return the debtors
     */
    public Set<String> getDebtors() {
        return this.debtors;
    }

    /**
     * The username-to-amount mapping.
     * @return the expense splits
     */
    public Map<String, Double> getExpenseSplits() {
        return this.expenseSplits;
    }
}

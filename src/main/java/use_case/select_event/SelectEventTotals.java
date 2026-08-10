package use_case.select_event;

/**
 * The money totals shown on the selected event's dashboard.
 */
public final class SelectEventTotals {

    private final double totalBudget;
    private final double totalSpent;
    private final double unsettledDebts;
    private final int expenseCount;
    private final int peopleOweCount;

    /**
     * Creates the totals for the selected event.
     *
     * @param totalBudget the total event budget
     * @param totalSpent the total amount spent
     * @param unsettledDebts the total unsettled debt
     * @param expenseCount the number of expenses
     * @param peopleOweCount the number of people who owe money
     */
    public SelectEventTotals(double totalBudget, double totalSpent, double unsettledDebts,
                             int expenseCount, int peopleOweCount) {
        this.totalBudget = totalBudget;
        this.totalSpent = totalSpent;
        this.unsettledDebts = unsettledDebts;
        this.expenseCount = expenseCount;
        this.peopleOweCount = peopleOweCount;
    }

    /**
     * The total event budget.
     * @return the total budget
     */
    public double getTotalBudget() {
        return this.totalBudget;
    }

    /**
     * The total amount spent.
     * @return the total spent
     */
    public double getTotalSpent() {
        return this.totalSpent;
    }

    /**
     * The total unsettled debt.
     * @return the unsettled debts
     */
    public double getUnsettledDebts() {
        return this.unsettledDebts;
    }

    /**
     * The number of expenses.
     * @return the expense count
     */
    public int getExpenseCount() {
        return this.expenseCount;
    }

    /**
     * The number of people who owe money.
     * @return the people-owe count
     */
    public int getPeopleOweCount() {
        return this.peopleOweCount;
    }
}

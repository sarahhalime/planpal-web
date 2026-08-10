package use_case.select_event;

/**
 * Raw per-expense data produced by the select event use case, to be formatted
 * for display by the presenter.
 */
public class SelectEventExpenseData {

    private final int expenseId;
    private final String expenseName;
    private final double totalAmount;
    private final SelectEventOriginalExpenseData originalExpenseData;
    private final String payerName;
    private final int splitCount;
    private final String status;

    /**
     * Creates expense output using the event amount as its original amount.
     *
     * @param id expense identifier
     * @param expenseName expense name
     * @param totalAmount event-currency amount
     * @param payerName payer username
     * @param splitCount number of split participants
     * @param status settlement status
     */
    public SelectEventExpenseData(
            int id,
            String expenseName,
            double totalAmount,
            String payerName,
            int splitCount,
            String status) {
        this(
                id,
                expenseName,
                totalAmount,
                new SelectEventOriginalExpenseData(totalAmount, "CAD"),
                payerName,
                splitCount,
                status
        );
    }

    /**
     * Creates raw expense output including its original input currency.
     *
     * @param id expense identifier
     * @param expenseName expense name
     * @param totalAmount event-currency amount
     * @param originalExpenseData original amount and currency
     * @param payerName payer username
     * @param splitCount number of split participants
     * @param status settlement status
     */
    public SelectEventExpenseData(
            int id,
            String expenseName,
            double totalAmount,
            SelectEventOriginalExpenseData originalExpenseData,
            String payerName,
            int splitCount,
            String status) {
        this.expenseId = id;
        this.expenseName = expenseName;
        this.totalAmount = totalAmount;
        this.originalExpenseData = originalExpenseData;
        this.payerName = payerName;
        this.splitCount = splitCount;
        this.status = status;
    }

    public int getExpenseId() {
        return this.expenseId;
    }

    public String getExpenseName() {
        return this.expenseName;
    }

    public double getTotalAmount() {
        return this.totalAmount;
    }

    /**
     * Returns the amount originally entered for the expense.
     *
     * @return original amount
     */
    public double getOriginalAmount() {
        return this.originalExpenseData.getAmount();
    }

    /**
     * Returns the currency originally selected for the expense.
     *
     * @return original currency code
     */
    public String getOriginalCurrency() {
        return this.originalExpenseData.getCurrencyCode();
    }

    public String getPayerName() {
        return this.payerName;
    }

    public int getSplitCount() {
        return this.splitCount;
    }

    public String getStatus() {
        return this.status;
    }
}

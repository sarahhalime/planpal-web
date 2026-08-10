package entity;

/**
 * Display data for one expense row.
 */
public class ExpensesData {
    private final int expenseId;
    private final String description;
    private final double amount;
    private String currencyCode;
    private double originalAmount;
    private String originalCurrency;
    private final String paidBy;
    private final String splitInfo;
    private String status;
    private final String date;

    /**
     * Creates expense display data.
     *
     * @param id expense identifier
     * @param description expense description
     * @param amount displayed amount
     * @param paidBy payer username
     * @param splitInfo split summary
     * @param status settlement status
     * @param date expense date
     */
    public ExpensesData(
            int id,
            String description,
            double amount,
            String paidBy,
            String splitInfo,
            String status,
            String date) {
        this.expenseId = id;
        this.description = description;
        this.amount = amount;
        this.currencyCode = "CAD";
        this.originalAmount = amount;
        this.originalCurrency = "CAD";
        this.paidBy = paidBy;
        this.splitInfo = splitInfo;
        this.status = status;
        this.date = date;
    }

    public int getExpenseId() {
        return this.expenseId;
    }

    public String getDescription() {
        return this.description;
    }

    public double getAmount() {
        return this.amount;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    /**
     * Returns the amount originally entered for the expense.
     *
     * @return original amount
     */
    public double getOriginalAmount() {
        return this.originalAmount;
    }

    /**
     * Returns the currency originally selected for the expense.
     *
     * @return original currency code
     */
    public String getOriginalCurrency() {
        return this.originalCurrency;
    }

    /**
     * Stores the original amount and currency for secondary display.
     *
     * @param amt original entered amount
     * @param curCode original selected currency
     */
    public void setOriginalValues(double amt, String curCode) {
        this.originalAmount = amt;
        this.originalCurrency = curCode;
    }

    public String getPaidBy() {
        return this.paidBy;
    }

    public String getSplitInfo() {
        return this.splitInfo;
    }

    public String getStatus() {
        return this.status;
    }

    public String getDate() {
        return this.date;
    }

    /**
     * Updates the settlement status.
     *
     * @param status the new status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the status of the expense to "PAID".
     */
    public void setPaid() {
        this.status = "PAID";
    }
}

package use_case.select_event;

/**
 * Stores the amount and currency originally entered for an expense.
 */
public final class SelectEventOriginalExpenseData {
    private final double amount;
    private final String currencyCode;

    /**
     * Creates original expense currency data.
     *
     * @param amount original entered amount
     * @param currencyCode original selected currency
     */
    public SelectEventOriginalExpenseData(double amount, String currencyCode) {
        this.amount = amount;
        this.currencyCode = currencyCode;
    }

    /**
     * Returns the original entered amount.
     *
     * @return original amount
     */
    public double getAmount() {
        return this.amount;
    }

    /**
     * Returns the original selected currency.
     *
     * @return original currency code
     */
    public String getCurrencyCode() {
        return this.currencyCode;
    }
}

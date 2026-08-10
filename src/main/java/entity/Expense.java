package entity;

import java.util.Map;
import java.util.Set;

/**
 * Represents an expense paid by an attendee.
 */
public interface Expense {

    /**
     * Returns the unique identifier of the expense.
     * @return the expense identifier
     */
    int getExpenseId();

    /**
     * Returns the name of the expense.
     * @return the expense name
     */
    String getExpenseName();

    /**
     * Returns the username of the attendee who paid this expense.
     * @return the username of the attendee who paid this expense.
     */
    String getPayerUsername();

    /**
     * Returns the total amount of the expense.
     * @return the total amount of the expense
     */
    double getTotalAmount();

    /**
     * Returns the amount originally entered by the payer.
     * @return the original entered amount
     */
    double getOriginalAmount();

    /**
     * Returns the currency originally selected by the payer.
     * @return the original currency code
     */
    String getOriginalCurrency();

    /**
     * Returns the custom split amounts in the original input currency.
     * @return username-to-amount split mapping in the original input currency
     */
    Map<String, Double> getOriginalExpenseSplits();

    /**
     * Updates the original input-currency values for this expense.
     * @param originalAmount the amount originally entered by the payer
     * @param originalCurrency the currency originally selected by the payer
     * @param originalExpenseSplits the split amounts in the original input currency
     */
    void setOriginalValues(
            double originalAmount,
            String originalCurrency,
            Map<String, Double> originalExpenseSplits);

    /**
     * Returns true if the expense has been split amongst multiple attendees not equally.
     * @return true if the expense has been split, false otherwise.
     */
    boolean isCustomSplit();

    /**
     * Returns the set of attendees who owe money for this expense.
     * @return the set of attendees who owe money for this expense.
     */
    Set<String> getDebtors();

    /**
     * Returns the expense splits.
     * @return the expense splits mapped from attendee usernames to their share of the expense.
     */
    Map<String, Double> getExpenseSplits();

    /**
     * Returns the status of the expense.
     * @return the status of the expense.
     */
    String getStatus();

    /**
     * Sets the status of the expense to "PAID".
     */
    void setStatusPaid();

    /**
     * Sets the status of the expense to "UNPAID".
     */
    void setStatusUnpaid();

}

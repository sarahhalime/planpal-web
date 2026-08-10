package use_case.pay_expense;

/**
 * Input data for toggling an expense's settlement status.
 */
public final class PayExpenseInputData {

    private final int eventId;
    private final int expenseId;

    /**
     * Creates the input data.
     *
     * @param eventId the selected event ID
     * @param expenseId the expense ID
     */
    public PayExpenseInputData(int eventId, int expenseId) {
        this.eventId = eventId;
        this.expenseId = expenseId;
    }

    /**
     * Returns the event ID.
     *
     * @return the event ID
     */
    public int getEventId() {
        return this.eventId;
    }

    /**
     * Returns the expense ID.
     *
     * @return the expense ID
     */
    public int getExpenseId() {
        return this.expenseId;
    }
}

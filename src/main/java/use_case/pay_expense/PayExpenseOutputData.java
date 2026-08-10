package use_case.pay_expense;

import entity.Event;

/**
 * Output data produced after toggling an expense's settlement status.
 */
public final class PayExpenseOutputData {

    private final Event event;
    private final int eventId;
    private final int expenseId;
    private final String status;
    private final double unsettledDebts;
    private final int peopleWhoOwe;

    /**
     * Creates the output data.
     *
     * @param event the event
     * @param eventId the selected event ID
     * @param expenseId the updated expense ID
     * @param status the updated status
     * @param unsettledDebts the remaining unsettled debt
     * @param peopleWhoOwe the number of people who still owe money
     */
    public PayExpenseOutputData(
            Event event,
            int eventId,
            int expenseId,
            String status,
            double unsettledDebts,
            int peopleWhoOwe
    ) {
        this.event = event;
        this.eventId = eventId;
        this.expenseId = expenseId;
        this.status = status;
        this.unsettledDebts = unsettledDebts;
        this.peopleWhoOwe = peopleWhoOwe;
    }

    public Event getEvent() {
        return this.event;
    }

    public int getEventId() {
        return this.eventId;
    }

    public int getExpenseId() {
        return this.expenseId;
    }

    public String getStatus() {
        return this.status;
    }

    public double getUnsettledDebts() {
        return this.unsettledDebts;
    }

    public int getPeopleWhoOwe() {
        return this.peopleWhoOwe;
    }
}

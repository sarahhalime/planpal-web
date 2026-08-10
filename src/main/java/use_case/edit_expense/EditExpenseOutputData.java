package use_case.edit_expense;

import entity.Event;

/**
 * The output data for the edit expense use case.
 * Get passed to the presenter so the view (and balances) can update.
 */
public class EditExpenseOutputData {

    private final Event event;
    private final String expenseName;
    private final double totalAmount;
    private final boolean useCaseFailed;

    public EditExpenseOutputData(Event event, String expenseName, double totalAmount,
                                 boolean useCaseFailed) {
        this.event = event;
        this.expenseName = expenseName;
        this.totalAmount = totalAmount;
        this.useCaseFailed = useCaseFailed;
    }

    public Event getEvent() {
        return event;
    }

    /**
     * Returns the identifier of the updated event.
     *
     * @return the event identifier
     */
    public int getEventId() {
        return event.getEventId();
    }

    public String getExpenseName() {
        return expenseName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public boolean isUseCaseFailed() {
        return useCaseFailed;
    }
}

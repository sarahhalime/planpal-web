package use_case.add_expense;

import entity.Event;

/**
 * The output data for the add expense use case.
 */
public class AddExpenseOutputData {

    private final Event event;
    private final String expenseName;
    private final double totalAmount;
    private final boolean useCaseFailed;

    public AddExpenseOutputData(Event event, String expenseName, double totalAmount,
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
     * Returns the ID of the updated event.
     *
     * @return the event ID
     */
    public int getEventId() {
        return this.event.getEventId();
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

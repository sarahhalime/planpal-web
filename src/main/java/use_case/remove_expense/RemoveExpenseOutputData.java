package use_case.remove_expense;

import entity.Event;

/**
 * The output data for the remove expense use case.
 */
public class RemoveExpenseOutputData {

    private final Event event;
    private final String expenseName;
    private final boolean useCaseFailed;

    public RemoveExpenseOutputData(Event event, String expenseName, boolean useCaseFailed) {
        this.event = event;
        this.expenseName = expenseName;
        this.useCaseFailed = useCaseFailed;
    }

    public Event getEvent() {
        return event;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public boolean isUseCaseFailed() {
        return useCaseFailed;
    }
}

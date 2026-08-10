package use_case.remove_expense;

/**
 * The input data for the remove expense use case.
 * It identifies the event and the expense (by id) to delete.
 */
public class RemoveExpenseInputData {

    private final int eventId;
    private final int expenseId;

    public RemoveExpenseInputData(int eventId, int expenseId) {
        this.eventId = eventId;
        this.expenseId = expenseId;
    }

    public int getEventId() {
        return eventId;
    }

    public int getExpenseId() {
        return expenseId;
    }
}

package use_case.remove_expense;

import java.util.ArrayList;
import java.util.List;

import entity.Event;
import entity.EventDetails;
import entity.EventFactory;
import entity.Expense;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/**
 * Removes an expense from an event.
 */
public final class RemoveExpenseInteractor implements RemoveExpenseInputBoundary {

    private static final String EXPENSE_NOT_FOUND_MESSAGE =
            "No expense was found with that id.";

    private final RemoveExpenseDataAccessInterface dataAccessObject;
    private final RemoveExpenseOutputBoundary presenter;
    private final EventFactory eventFactory;

    /**
     * Creates a remove-expense interactor.
     *
     * @param dataAccessObject the event data-access object
     * @param presenter the remove-expense output boundary
     * @param eventFactory the event factory
     */
    public RemoveExpenseInteractor(
            RemoveExpenseDataAccessInterface dataAccessObject,
            RemoveExpenseOutputBoundary presenter,
            EventFactory eventFactory) {

        this.dataAccessObject = dataAccessObject;
        this.presenter = presenter;
        this.eventFactory = eventFactory;
    }

    /**
     * Removes the requested expense from its event.
     *
     * @param inputData the remove-expense input data
     */
    @Override
    public void execute(RemoveExpenseInputData inputData) {
        try {
            final Event event = this.dataAccessObject.getEvent(
                    inputData.getEventId()
            );

            final List<Expense> updatedExpenses = new ArrayList<>();
            String removedExpenseName = null;

            for (final Expense expense : event.getExpenseList()) {
                if (expense.getExpenseId() == inputData.getExpenseId()) {
                    removedExpenseName = expense.getExpenseName();
                }
                else {
                    updatedExpenses.add(expense);
                }
            }

            if (removedExpenseName == null) {
                this.presenter.prepareFailView(
                        EXPENSE_NOT_FOUND_MESSAGE
                );
            }
            else {
                this.saveUpdatedEvent(
                        event,
                        updatedExpenses,
                        removedExpenseName
                );
            }
        }
        catch (WhoOwesWhatDataAccessException exception) {
            this.presenter.prepareFailView(exception.getMessage());
        }
    }

    private void saveUpdatedEvent(
            Event event,
            List<Expense> updatedExpenses,
            String removedExpenseName) {

        final Event updatedEvent = this.eventFactory.createEvent(
                                                            event.getEventId(),
                                                            new EventDetails(
                                                                    event.getEventName(),
                                                                    event.getEventDescription(),
                                                                    event.getEventLocation(),
                                                                    event.getEventBudget(),
                                                                    event.getEventCurrency()),
                                                            event.getEventSchedule(),
                                                            event.getAttendeeUsernames(),
                                                            updatedExpenses,
                                                            event.getActivityList()
                                                        );

        this.dataAccessObject.saveEvent(updatedEvent);

        final RemoveExpenseOutputData outputData =
                new RemoveExpenseOutputData(
                        updatedEvent,
                        removedExpenseName,
                        false
                );

        this.presenter.prepareSuccessView(outputData);
    }
}

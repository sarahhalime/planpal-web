package use_case.pay_expense;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import entity.Event;
import entity.Expense;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/**
 * Toggles an expense between paid and unpaid.
 */
public final class PayExpenseInteractor implements PayExpenseInputBoundary {

    private static final String PAID_STATUS = "PAID";
    private static final double BALANCE_TOLERANCE = 0.001;
    private static final int ZERO_COUNT = 0;

    private final PayExpenseDataAccessInterface dataAccessObject;
    private final PayExpenseOutputBoundary presenter;

    public PayExpenseInteractor(
            PayExpenseDataAccessInterface dataAccessObject,
            PayExpenseOutputBoundary presenter
    ) {
        this.dataAccessObject = dataAccessObject;
        this.presenter = presenter;
    }

    @Override
    public void execute(PayExpenseInputData inputData) {
        try {
            final Event event = this.dataAccessObject.getEvent(inputData.getEventId());
            final Expense expense = this.findExpense(event, inputData.getExpenseId());

            if (PAID_STATUS.equalsIgnoreCase(expense.getStatus())) {
                expense.setStatusUnpaid();
            }
            else {
                expense.setStatusPaid();
            }

            this.dataAccessObject.saveEvent(event);

            final BalanceSummary summary = this.calculateBalanceSummary(event);
            final PayExpenseOutputData outputData = new PayExpenseOutputData(
                    event,
                    event.getEventId(),
                    expense.getExpenseId(),
                    expense.getStatus(),
                    summary.totalOwed,
                    summary.peopleWhoOwe
            );

            this.presenter.prepareSuccessView(outputData);
        }
        catch (WhoOwesWhatDataAccessException | IllegalArgumentException exception) {
            this.presenter.prepareFailView(exception.getMessage());
        }
    }

    private Expense findExpense(Event event, int expenseId) {
        Expense result = null;

        for (final Expense expense : event.getExpenseList()) {
            if (expense.getExpenseId() == expenseId) {
                result = expense;
            }
        }

        if (result == null) {
            throw new IllegalArgumentException("The selected expense could not be found.");
        }

        return result;
    }

    private BalanceSummary calculateBalanceSummary(Event event) {
        final Map<String, Double> balances = new ConcurrentHashMap<>();

        for (final String username : event.getAttendeeUsernames()) {
            balances.put(username, 0.0);
        }

        for (final Expense expense : event.getExpenseList()) {
            if (!PAID_STATUS.equalsIgnoreCase(expense.getStatus())) {
                balances.merge(
                        expense.getPayerUsername(),
                        expense.getTotalAmount(),
                        Double::sum
                );

                for (final Map.Entry<String, Double> split : expense.getExpenseSplits().entrySet()) {
                    balances.merge(split.getKey(), -split.getValue(), Double::sum);
                }
            }
        }

        double totalOwed = 0.0;
        int peopleWhoOwe = ZERO_COUNT;

        for (final double balance : balances.values()) {
            if (balance < -BALANCE_TOLERANCE) {
                totalOwed += Math.abs(balance);
                peopleWhoOwe++;
            }
        }

        return new BalanceSummary(totalOwed, peopleWhoOwe);
    }

    private static final class BalanceSummary {

        private final double totalOwed;
        private final int peopleWhoOwe;

        private BalanceSummary(double totalOwed, int peopleWhoOwe) {
            this.totalOwed = totalOwed;
            this.peopleWhoOwe = peopleWhoOwe;
        }
    }
}

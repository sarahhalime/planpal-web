package use_case.select_event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import entity.Activity;
import entity.Event;
import entity.EventScheduleInput;
import entity.Expense;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/**
 * Loads the details of a selected event.
 */
public final class SelectEventInteractor implements SelectEventInputBoundary {

    private static final String EVENT_ERROR_MESSAGE =
            "The selected event could not be loaded.";
    private static final double BALANCE_TOLERANCE = 0.001;
    private static final int ZERO_COUNT = 0;
    private static final String PAID_STATUS = "PAID";

    private final SelectEventDataAccessInterface dataAccessObject;
    private final SelectEventOutputBoundary presenter;

    /**
     * Creates a select-event interactor.
     *
     * @param dataAccessObject the event data-access object
     * @param presenter the select-event output boundary
     */
    public SelectEventInteractor(
            SelectEventDataAccessInterface dataAccessObject,
            SelectEventOutputBoundary presenter) {

        this.dataAccessObject = dataAccessObject;
        this.presenter = presenter;
    }

    /**
     * Loads the event identified by the supplied input data.
     *
     * @param inputData the select-event input data
     */
    @Override
    public void execute(SelectEventInputData inputData) {
        try {
            final Event event = this.dataAccessObject.getEvent(
                    inputData.getNewSelectedEventId()
            );

            this.presenter.prepareSuccessView(
                    this.buildOutputData(event)
            );
        }
        catch (WhoOwesWhatDataAccessException exception) {
            this.presenter.prepareFailureView(EVENT_ERROR_MESSAGE);
        }
    }

    private SelectEventOutputData buildOutputData(Event event) {
        final double totalBudget = this.getBudget(event);
        final double totalSpent = this.calculateTotalSpent(event);
        final BalanceSummary balanceSummary =
                this.calculateBalanceSummary(event);

        final SelectEventDetails details = new SelectEventDetails(
                event.getEventId(),
                event.getEventName(),
                event.getEventDescription(),
                event.getEventLocation(),
                event.getEventCurrency(),
                new EventScheduleInput(
                        event.getStartDate(),
                        event.getStartTime(),
                        event.getEndDate(),
                        event.getEndTime()
                )
        );
        final SelectEventTotals totals = new SelectEventTotals(
                totalBudget,
                totalSpent,
                balanceSummary.totalOwed,
                event.getExpenseList().size(),
                balanceSummary.peopleWhoOwe
        );
        return new SelectEventOutputData(
                details,
                totals,
                this.createExpenseRows(event),
                this.createActivityRows(event),
                this.dataAccessObject.getEventPhoto(event.getEventId())
        );
    }

    private List<SelectEventExpenseData> createExpenseRows(Event event) {
        final List<SelectEventExpenseData> rows = new ArrayList<>();

        for (final Expense expense : event.getExpenseList()) {
            rows.add(new SelectEventExpenseData(
                    expense.getExpenseId(),
                    expense.getExpenseName(),
                    expense.getTotalAmount(),
                    new SelectEventOriginalExpenseData(
                            expense.getOriginalAmount(),
                            expense.getOriginalCurrency()
                    ),
                    expense.getPayerUsername(),
                    expense.getExpenseSplits().size(),
                    expense.getStatus()
            ));
        }

        return rows;
    }

    private List<SelectEventActivityData> createActivityRows(Event event) {
        final List<SelectEventActivityData> rows = new ArrayList<>();

        for (final Activity activity : event.getActivityList()) {
            rows.add(new SelectEventActivityData(
                    activity.getActivityName(),
                    activity.getDate(),
                    activity.getTime(),
                    activity.getLocation()
            ));
        }

        return rows;
    }

    private double getBudget(Event event) {
        final Double storedBudget = event.getEventBudget();
        final double budget;

        if (storedBudget == null) {
            budget = 0.0;
        }
        else {
            budget = storedBudget;
        }

        return budget;
    }

    private double calculateTotalSpent(Event event) {
        double totalSpent = 0.0;

        for (final Expense expense : event.getExpenseList()) {
            totalSpent += expense.getTotalAmount();
        }

        return totalSpent;
    }

    /**
     * Balances are keyed by attendee username because expenses store payer
     * usernames and username-to-amount split maps.
     *
     * @param event the selected event
     * @return the balance summary for the event
     */
    private BalanceSummary calculateBalanceSummary(Event event) {
        final Map<String, Double> balances =
                new ConcurrentHashMap<>();

        for (final String username : event.getAttendeeUsernames()) {
            balances.put(username, 0.0);
        }

        this.applyExpenseBalances(event, balances);

        return this.summarizeBalances(balances);
    }

    private void applyExpenseBalances(
            Event event,
            Map<String, Double> balances) {

        for (final Expense expense : event.getExpenseList()) {
            if (!PAID_STATUS.equalsIgnoreCase(expense.getStatus())) {
                balances.merge(
                        expense.getPayerUsername(),
                        expense.getTotalAmount(),
                        Double::sum
                );

                for (final Map.Entry<String, Double> split
                        : expense.getExpenseSplits().entrySet()) {

                    balances.merge(
                            split.getKey(),
                            -split.getValue(),
                            Double::sum
                    );
                }
            }
        }
    }

    private BalanceSummary summarizeBalances(
            Map<String, Double> balances) {

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

    /**
     * Stores the calculated debt summary for a selected event.
     */
    private static final class BalanceSummary {

        private final double totalOwed;
        private final int peopleWhoOwe;

        private BalanceSummary(
                double totalOwed,
                int peopleWhoOwe) {

            this.totalOwed = totalOwed;
            this.peopleWhoOwe = peopleWhoOwe;
        }
    }
}

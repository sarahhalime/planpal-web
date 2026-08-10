package use_case.add_expense;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import entity.Event;
import entity.EventDetails;
import entity.EventFactory;
import entity.Expense;
import entity.ExpenseFactory;
import use_case.convert_currency.ConvertCurrencyDataAccessInterface;
import use_case.convert_currency.ExchangeRateData;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/**
 * Adds an expense to an existing event.
 */
public class AddExpenseInteractor implements AddExpenseInputBoundary {

    private static final String NO_PAYER_MESSAGE =
            "The payer cannot be empty.";
    private static final String DEBTORS_NOT_ATTENDING_EVENT_MESSAGE =
            "A selected debtors is not attending this event.";
    private static final String NO_DEBTORS_MESSAGE =
            "An expense must be split among at least one attendee.";
    private static final String NON_POSITIVE_AMOUNT_MESSAGE =
            "The expense amount must be greater than zero.";
    private static final String PAYER_NOT_ATTENDEE_MESSAGE =
            "The payer must be an attendee of this event.";
    private static final String INCOMPLETE_SPLIT_MESSAGE =
            "The expense split does not total to the expense amount.";

    private final AddExpenseDataAccessInterface dataAccessObject;
    private final AddExpenseOutputBoundary presenter;
    private final ExpenseFactory expenseFactory;
    private final EventFactory eventFactory;
    private final ConvertCurrencyDataAccessInterface currencyDataAccessObject;

    /**
     * Creates an add-expense interactor.
     *
     * @param dataAccessObject the event data-access object
     * @param presenter the add-expense output boundary
     * @param expenseFactory the expense factory
     * @param eventFactory the event factory
     */
    public AddExpenseInteractor(
            AddExpenseDataAccessInterface dataAccessObject,
            AddExpenseOutputBoundary presenter,
            ExpenseFactory expenseFactory,
            EventFactory eventFactory) {
        this(dataAccessObject, presenter, expenseFactory, eventFactory, null);
    }

    /**
     * Creates an add-expense interactor.
     *
     * @param dataAccessObject the event data-access object
     * @param presenter the add-expense output boundary
     * @param expenseFactory the expense factory
     * @param eventFactory the event factory
     * @param currencyDataAccessObject the currency conversion data-access object
     */
    public AddExpenseInteractor(
            AddExpenseDataAccessInterface dataAccessObject,
            AddExpenseOutputBoundary presenter,
            ExpenseFactory expenseFactory,
            EventFactory eventFactory,
            ConvertCurrencyDataAccessInterface currencyDataAccessObject) {

        this.dataAccessObject = dataAccessObject;
        this.presenter = presenter;
        this.expenseFactory = expenseFactory;
        this.eventFactory = eventFactory;
        this.currencyDataAccessObject = currencyDataAccessObject;
    }

    /**
     * Adds an expense using the supplied input data.
     *
     * @param inputData the add-expense input data
     */
    @Override
    public void execute(AddExpenseInputData inputData) {
        final boolean isCustomSplit = inputData.isCustomSplit();
        final Set<String> debtors = inputData.getDebtors();
        final Map<String, Double> customSplit = inputData.getCustomSplit();

        if (isPayerValid(inputData) != null) {
            this.presenter.prepareFailView(isPayerValid(inputData));
        }
        else if (isEqualSplitValid(inputData, isCustomSplit, debtors) != null) {
            this.presenter.prepareFailView(isEqualSplitValid(inputData, isCustomSplit, debtors));
        }
        else if (isCustomSplitValid(inputData, isCustomSplit, customSplit) != null) {
            this.presenter.prepareFailView(isCustomSplitValid(inputData, isCustomSplit, customSplit));
        }
        else {
            this.addExpense(inputData, isCustomSplit, debtors, customSplit);
        }
    }

    private String isCustomSplitValid(AddExpenseInputData inputData, boolean isCustomSplit,
                                      Map<String, Double> customSplit) {
        final String result;
        if (isCustomSplit && (customSplit == null || customSplit.isEmpty())) {
            result = NO_DEBTORS_MESSAGE;
        }
        else if (isCustomSplit && this.doesNonAttendingDebtorExist(inputData.getEventId(), customSplit.keySet())) {
            result = DEBTORS_NOT_ATTENDING_EVENT_MESSAGE;
        }
        else if (isCustomSplit && !this.isCompleteSplit(inputData.getTotalAmount(),
                new ArrayList<>(customSplit.values()))) {
            result = INCOMPLETE_SPLIT_MESSAGE;
        }
        else {
            result = null;
        }
        return result;
    }

    private String isEqualSplitValid(AddExpenseInputData inputData, boolean isCustomSplit, Set<String> debtors) {
        final String result;
        if (!isCustomSplit && (debtors == null || debtors.isEmpty())) {
            result = NO_DEBTORS_MESSAGE;
        }
        else if (!isCustomSplit && this.doesNonAttendingDebtorExist(inputData.getEventId(), debtors)) {
            result = DEBTORS_NOT_ATTENDING_EVENT_MESSAGE;
        }
        else {
            result = null;
        }
        return result;
    }

    private String isPayerValid(AddExpenseInputData inputData) {
        final String result;
        if (inputData.getTotalAmount() <= 0) {
            result = NON_POSITIVE_AMOUNT_MESSAGE;
        }
        else if (inputData.getPayerUsername() == null || inputData.getPayerUsername().isBlank()) {
            result = NO_PAYER_MESSAGE;
        }
        else if (!dataAccessObject.isAttendingEvent(inputData.getEventId(), inputData.getPayerUsername())) {
            result = PAYER_NOT_ATTENDEE_MESSAGE;
        }
        else {
            result = null;
        }
        return result;
    }

    private boolean isCompleteSplit(Double totalAmount, List<Double> splitAmounts) {
        Double splitTotal = 0.0;
        for (Double split : splitAmounts) {
            splitTotal += split;
        }
        return splitTotal.equals(totalAmount);
    }
    
    private boolean doesNonAttendingDebtorExist(int eventId,
            Set<String> debtors) {
        boolean result = false;
        for (final String debtor : debtors) {
            if (!dataAccessObject.isAttendingEvent(eventId, debtor)) {
                result = true;
                break;
            }
        }
        return result;
    }
    
    private void addExpense(
            AddExpenseInputData inputData,
            boolean isCustomSplit,
            Set<String> debtors,
            Map<String, Double> customSplit) {

        try {
            final Event event = this.dataAccessObject.getEvent(
                    inputData.getEventId()
            );
            this.saveExpense(
                    inputData,
                    event,
                    isCustomSplit,
                    debtors,
                    customSplit
            );
        }
        catch (WhoOwesWhatDataAccessException exception) {
            this.presenter.prepareFailView(exception.getMessage());
        }
    }

    private void saveExpense(
            AddExpenseInputData inputData,
            Event event,
            boolean isCustomSplit,
            Set<String> debtors,
            Map<String, Double> customSplit) {

        final double convertedTotalAmount = this.convertAmount(
                inputData.getTotalAmount(),
                inputData.getExpenseCurrency(),
                event.getEventCurrency()
        );
        final Map<String, Double> expenseSplits = this.buildSplits(
                isCustomSplit,
                debtors,
                customSplit,
                convertedTotalAmount,
                inputData.getExpenseCurrency(),
                event.getEventCurrency()
        );
        final Map<String, Double> originalExpenseSplits =
                this.buildOriginalSplits(
                        isCustomSplit,
                        debtors,
                        customSplit,
                        inputData.getTotalAmount()
                );

        final Set<String> resolvedDebtors;

        if (isCustomSplit) {
            resolvedDebtors = customSplit.keySet();
        }
        else {
            resolvedDebtors = debtors;
        }

        final Expense expense = this.expenseFactory.create(
                this.nextExpenseId(event),
                inputData.getExpenseName(),
                inputData.getPayerUsername(),
                convertedTotalAmount,
                isCustomSplit,
                resolvedDebtors,
                expenseSplits
        );
        expense.setOriginalValues(
                inputData.getTotalAmount(),
                inputData.getExpenseCurrency(),
                originalExpenseSplits
        );

        final List<Expense> updatedExpenses =
                new ArrayList<>(event.getExpenseList());

        updatedExpenses.add(expense);

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

        final AddExpenseOutputData outputData =
                new AddExpenseOutputData(
                        updatedEvent,
                        expense.getExpenseName(),
                        expense.getTotalAmount(),
                        false
                );

        this.presenter.prepareSuccessView(outputData);
    }

    private Map<String, Double> buildSplits(
            boolean isCustomSplit,
            Set<String> debtors,
            Map<String, Double> customSplit,
            double totalAmount,
            String sourceCurrency,
            String targetCurrency) {

        final Map<String, Double> splits =
                new ConcurrentHashMap<>();

        if (isCustomSplit) {
            for (final Map.Entry<String, Double> split : customSplit.entrySet()) {
                splits.put(
                        split.getKey(),
                        this.convertAmount(
                                split.getValue(),
                                sourceCurrency,
                                targetCurrency
                        )
                );
            }
        }
        else {
            final double shareAmount =
                    totalAmount / debtors.size();

            for (final String debtor : debtors) {
                splits.put(debtor, shareAmount);
            }
        }

        return splits;
    }

    private Map<String, Double> buildOriginalSplits(
            boolean isCustomSplit,
            Set<String> debtors,
            Map<String, Double> customSplit,
            double totalAmount) {

        final Map<String, Double> splits =
                new ConcurrentHashMap<>();

        if (isCustomSplit) {
            splits.putAll(customSplit);
        }
        else {
            final double shareAmount =
                    totalAmount / debtors.size();

            for (final String debtor : debtors) {
                splits.put(debtor, shareAmount);
            }
        }

        return splits;
    }

    private double convertAmount(
            double amount,
            String sourceCurrency,
            String targetCurrency) {

        final double convertedAmount;

        if (sourceCurrency == null || targetCurrency == null
                || sourceCurrency.equalsIgnoreCase(targetCurrency)
                || this.currencyDataAccessObject == null) {
            convertedAmount = amount;
        }
        else {
            final ExchangeRateData rateData =
                    this.currencyDataAccessObject.getExchangeRate(
                            sourceCurrency,
                            targetCurrency
                    );
            convertedAmount = BigDecimal.valueOf(amount)
                    .multiply(rateData.getRate())
                    .doubleValue();
        }

        return convertedAmount;
    }

    private int nextExpenseId(Event event) {
        int maximumId = 0;

        for (final Expense expense : event.getExpenseList()) {
            if (expense.getExpenseId() > maximumId) {
                maximumId = expense.getExpenseId();
            }
        }

        return maximumId + 1;
    }
}

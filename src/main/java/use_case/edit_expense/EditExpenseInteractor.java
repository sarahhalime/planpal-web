package use_case.edit_expense;

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
 * Edits an existing expense in an event.
 */
public final class EditExpenseInteractor implements EditExpenseInputBoundary {

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

    private final EditExpenseDataAccessInterface dataAccessObject;
    private final EditExpenseOutputBoundary presenter;
    private final ExpenseFactory expenseFactory;
    private final EventFactory eventFactory;
    private final ConvertCurrencyDataAccessInterface currencyDataAccessObject;

    /**
     * Creates an edit-expense interactor.
     *
     * @param dataAccessObject the event data-access object
     * @param presenter the edit-expense output boundary
     * @param expenseFactory the expense factory
     * @param eventFactory the event factory
     */
    public EditExpenseInteractor(
            EditExpenseDataAccessInterface dataAccessObject,
            EditExpenseOutputBoundary presenter,
            ExpenseFactory expenseFactory,
            EventFactory eventFactory) {
        this(dataAccessObject, presenter, expenseFactory, eventFactory, null);
    }

    /**
     * Creates an edit-expense interactor.
     *
     * @param dataAccessObject the event data-access object
     * @param presenter the edit-expense output boundary
     * @param expenseFactory the expense factory
     * @param eventFactory the event factory
     * @param currencyDataAccessObject the currency conversion data-access object
     */
    public EditExpenseInteractor(
            EditExpenseDataAccessInterface dataAccessObject,
            EditExpenseOutputBoundary presenter,
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
     * Loads the existing values of the expense being edited.
     *
     * @param eventId selected event identifier
     * @param expenseId expense identifier
     */
    @Override
    public void setUpFields(int eventId, int expenseId) {
        try {
            final Event event = this.dataAccessObject.getEvent(eventId);
            final Expense expense = this.findExpense(event, expenseId);

            if (expense == null) {
                this.presenter.prepareFailView(
                        "No expense was found with that id."
                );
            }
            else {
                this.presenter.preparePrefillView(
                        this.createPrefillData(expense)
                );
            }
        }
        catch (WhoOwesWhatDataAccessException exception) {
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

        return result;
    }

    private EditExpensePrefillData createPrefillData(Expense expense) {
        return new EditExpensePrefillData(
                expense.getExpenseId(),
                expense.getExpenseName(),
                expense.getOriginalAmount(),
                expense.getOriginalCurrency(),
                expense.getPayerUsername(),
                new ExpenseSplitDetails(
                    expense.isCustomSplit(),
                    expense.getDebtors(),
                    expense.getOriginalExpenseSplits()
        ),
                expense.getStatus()
        );
    }

    /**
     * Edits an expense using the supplied input data.
     *
     * @param inputData the edit-expense input data
     */
    @Override
    public void execute(EditExpenseInputData inputData) {
        final boolean isCustomSplit = inputData.isCustomSplit();
        final Set<String> debtors = inputData.getDebtors();
        final Map<String, Double> customSplit = inputData.getCustomSplit();

        final String localValidationError =
                this.validateLocalInput(
                        inputData,
                        isCustomSplit,
                        debtors,
                        customSplit
                );

        if (localValidationError != null) {
            this.presenter.prepareFailView(localValidationError);
        }
        else {
            this.editExpense(
                    inputData,
                    isCustomSplit,
                    debtors,
                    customSplit
            );
        }
    }

    private String validateLocalInput(
            EditExpenseInputData inputData,
            boolean isCustomSplit,
            Set<String> debtors,
            Map<String, Double> customSplit
    ) {
        final String result;

        if (inputData.getExpensesData().getAmount() <= 0) {
            result = NON_POSITIVE_AMOUNT_MESSAGE;
        }
        else if (this.hasNoPayer(inputData)) {
            result = NO_PAYER_MESSAGE;
        }
        else {
            result = this.validateSplit(inputData, isCustomSplit, debtors, customSplit);
        }

        return result;
    }

    private boolean hasNoPayer(EditExpenseInputData inputData) {
        return inputData.getExpensesData().getPaidBy() == null
                || inputData.getExpensesData().getPaidBy().isBlank();
    }

    private String validateSplit(
            EditExpenseInputData inputData,
            boolean isCustomSplit,
            Set<String> debtors,
            Map<String, Double> customSplit
    ) {
        final String result;

        if (!isCustomSplit && (debtors == null || debtors.isEmpty())) {
            result = NO_DEBTORS_MESSAGE;
        }
        else if (isCustomSplit && (customSplit == null || customSplit.isEmpty())) {
            result = NO_DEBTORS_MESSAGE;
        }
        else if (isCustomSplit
                && !this.isCompleteSplit(
                inputData.getExpensesData().getAmount(),
                new ArrayList<>(customSplit.values())
        )) {
            result = INCOMPLETE_SPLIT_MESSAGE;
        }
        else {
            result = null;
        }

        return result;
    }

    private void editExpense(
            EditExpenseInputData inputData,
            boolean isCustomSplit,
            Set<String> debtors,
            Map<String, Double> customSplit
    ) {
        try {
            final Event event = this.dataAccessObject.getEvent(
                    inputData.getEventId()
            );
            final String eventValidationError =
                    this.validateAgainstEvent(
                            inputData,
                            event,
                            isCustomSplit,
                            debtors,
                            customSplit
                    );

            if (eventValidationError != null) {
                this.presenter.prepareFailView(eventValidationError);
            }
            else {
                this.updateExpense(
                        inputData,
                        event,
                        isCustomSplit,
                        debtors,
                        customSplit
                );
            }
        }
        catch (WhoOwesWhatDataAccessException exception) {
            this.presenter.prepareFailView(exception.getMessage());
        }
    }

    private String validateAgainstEvent(
            EditExpenseInputData inputData,
            Event event,
            boolean isCustomSplit,
            Set<String> debtors,
            Map<String, Double> customSplit
    ) {
        final String result;
        final String payer =
                inputData.getExpensesData().getPaidBy();
        final Set<String> resolvedDebtors;

        if (isCustomSplit) {
            resolvedDebtors = customSplit.keySet();
        }
        else {
            resolvedDebtors = debtors;
        }

        if (!event.getAttendeeUsernames().contains(payer)) {
            result = PAYER_NOT_ATTENDEE_MESSAGE;
        }
        else if (this.doesNonAttendingDebtorExist(
                event,
                resolvedDebtors
        )) {
            result = DEBTORS_NOT_ATTENDING_EVENT_MESSAGE;
        }
        else if (this.findExpense(
                event,
                inputData.getExpensesData().getExpenseId()
        ) == null) {
            result = "No expense was found with that id.";
        }
        else {
            result = null;
        }

        return result;
    }

    private void updateExpense(
            EditExpenseInputData inputData,
            Event event,
            boolean isCustomSplit,
            Set<String> debtors,
            Map<String, Double> customSplit
    ) {
        final double convertedTotalAmount = this.convertAmount(
                inputData.getExpensesData().getAmount(),
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
                        inputData.getExpensesData().getAmount()
                );
        final Set<String> resolvedDebtors;

        if (isCustomSplit) {
            resolvedDebtors = customSplit.keySet();
        }
        else {
            resolvedDebtors = debtors;
        }

        final List<Expense> updatedExpenses = new ArrayList<>();

        for (final Expense existingExpense : event.getExpenseList()) {
            if (existingExpense.getExpenseId()
                    == inputData.getExpensesData().getExpenseId()) {

                final Expense updatedExpense =
                        this.expenseFactory.create(
                                inputData.getExpensesData().getExpenseId(),
                                inputData.getExpensesData().getDescription(),
                                inputData.getExpensesData().getPaidBy(),
                                convertedTotalAmount,
                                isCustomSplit,
                                resolvedDebtors,
                                expenseSplits
                        );
                updatedExpense.setOriginalValues(
                        inputData.getExpensesData().getAmount(),
                        inputData.getExpenseCurrency(),
                        originalExpenseSplits
                );

                if ("PAID".equals(
                        inputData.getExpensesData().getStatus()
                )) {
                    updatedExpense.setStatusPaid();
                }
                else {
                    updatedExpense.setStatusUnpaid();
                }

                updatedExpenses.add(updatedExpense);
            }
            else {
                updatedExpenses.add(existingExpense);
            }
        }

        this.saveUpdatedEvent(
                inputData,
                event,
                updatedExpenses
        );
    }

    private void saveUpdatedEvent(
            EditExpenseInputData inputData,
            Event event,
            List<Expense> updatedExpenses
    ) {
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

        final EditExpenseOutputData outputData =
                new EditExpenseOutputData(
                        updatedEvent,
                        inputData.getExpensesData().getDescription(),
                        inputData.getExpensesData().getAmount(),
                        false
                );

        this.presenter.prepareSuccessView(outputData);
    }

    private boolean isCompleteSplit(
            Double totalAmount,
            List<Double> splitAmounts
    ) {
        double splitTotal = 0.0;

        for (final Double split : splitAmounts) {
            splitTotal += split;
        }

        return Double.compare(splitTotal, totalAmount) == 0;
    }

    private boolean doesNonAttendingDebtorExist(
            Event event,
            Set<String> debtors
    ) {
        boolean result = false;

        for (final String debtor : debtors) {
            if (!event.getAttendeeUsernames().contains(debtor)) {
                result = true;
                break;
            }
        }

        return result;
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
}

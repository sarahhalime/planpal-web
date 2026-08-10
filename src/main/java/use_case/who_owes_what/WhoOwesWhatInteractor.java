package use_case.who_owes_what;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.Event;
import entity.Expense;

public class WhoOwesWhatInteractor implements WhoOwesWhatInputBoundary {

    private static final String PAID_STATUS = "PAID";

    private final WhoOwesWhatDataAccessInterface dataAccessInterface;
    private final WhoOwesWhatOutputBoundary outputBoundary;
    private final WhoOwesWhatProfileDataAccessInterface profileDataAccessInterface;

    public WhoOwesWhatInteractor(WhoOwesWhatDataAccessInterface dataAccessInterface,
                                 WhoOwesWhatOutputBoundary outputBoundary,
                                 WhoOwesWhatProfileDataAccessInterface profileDataAccessInterface) {
        this.dataAccessInterface = dataAccessInterface;
        this.outputBoundary = outputBoundary;
        this.profileDataAccessInterface = profileDataAccessInterface;
    }

    private String decideBalanceStatus(double balance) {
        String result = "SETTLED";
        if (balance > 0) {
            result = "IS_OWED";
        }
        else if (balance < 0) {
            result = "OWES";
        }
        return result;
    }

    // Keyed by username now, not attendee id.
    private List<AttendeeBalanceOutputData> generateAttendeeBalanceOutputDataList(
            Event event, Map<String, Double> usernameToBalances,
            Map<String, List<ExpenseOutputData>> usernameToExpenses) {

        final List<AttendeeBalanceOutputData> listToReturn = new ArrayList<>();

        for (final String username : event.getAttendeeUsernames()) {
            final double balance = usernameToBalances.get(username);
            listToReturn.add(new AttendeeBalanceOutputData(
                    username,
                    Math.abs(balance),
                    decideBalanceStatus(balance),
                    usernameToExpenses.get(username),
                    this.profileDataAccessInterface.getProfilePicture(username)
            ));
        }

        return listToReturn;
    }

    private void subtractBalanceByShare(Expense expense, Map<String, Double> usernameToBalances,
                                        Map<String, List<ExpenseOutputData>> usernameToExpenses) {
        for (final Map.Entry<String, Double> split : expense.getExpenseSplits().entrySet()) {
            final String username = split.getKey();
            final double shareAmount = split.getValue();

            usernameToBalances.merge(username, -shareAmount, Double::sum);
            usernameToExpenses.computeIfAbsent(username, key -> new ArrayList<>())
                    .add(new ExpenseOutputData(expense.getExpenseName(), shareAmount));
        }
    }

    private Map<String, Double> generateUsernameToBalancesMap(Event event) {
        final Map<String, Double> map = new HashMap<>();
        for (final String username : event.getAttendeeUsernames()) {
            map.put(username, 0.0);
        }
        return map;
    }

    private Map<String, List<ExpenseOutputData>> generateUsernameToExpensesMap(Event event) {
        final Map<String, List<ExpenseOutputData>> map = new HashMap<>();
        for (final String username : event.getAttendeeUsernames()) {
            map.put(username, new ArrayList<>());
        }
        return map;
    }

    @Override
    public void execute(WhoOwesWhatInputData inputData) {
        try {
            final Event thisEvent = dataAccessInterface.getEvent(inputData.getEventId());
            final Map<String, Double> usernameToBalances = generateUsernameToBalancesMap(thisEvent);
            final Map<String, List<ExpenseOutputData>> usernameToExpenses = generateUsernameToExpensesMap(thisEvent);

            for (final Expense expense : thisEvent.getExpenseList()) {
                if (!PAID_STATUS.equalsIgnoreCase(expense.getStatus())) {
                    usernameToBalances.merge(
                            expense.getPayerUsername(),
                            expense.getTotalAmount(),
                            Double::sum
                    );
                    subtractBalanceByShare(
                            expense,
                            usernameToBalances,
                            usernameToExpenses
                    );
                }
            }

            final List<AttendeeBalanceOutputData> list =
                    generateAttendeeBalanceOutputDataList(thisEvent, usernameToBalances, usernameToExpenses);

            outputBoundary.prepareSuccessView(new WhoOwesWhatOutputData(
                    thisEvent.getEventName(),
                    thisEvent.getEventCurrency(),
                    list
            ));
        }
        catch (WhoOwesWhatDataAccessException exception) {
            outputBoundary.prepareFailView(exception.getMessage());
        }
    }
}

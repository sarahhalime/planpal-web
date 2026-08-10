package use_case.login;

import java.util.ArrayList;
import java.util.List;

import data_access.PasswordHasher;
import entity.Event;
import entity.EventSummary;
import entity.User;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

public class LoginInteractor implements LoginInputBoundary {

    // Has a Presenter which it will send output data to
    // Has userDataAccessObject which contains database

    private final LoginDataAccessInterface userDataAccessObject;
    private final LoginEventDataAccessInterface loginEventDataAccessInterface;
    private final LoginOutputBoundary loginPresenter;

    // Assign Presenter and userDataAccessObject
    public LoginInteractor(LoginDataAccessInterface userDataAccessObject,
                           LoginEventDataAccessInterface loginEventDataAccessInterface,
                           LoginOutputBoundary loginPresenter) {
        this.userDataAccessObject = userDataAccessObject;
        this.loginEventDataAccessInterface = loginEventDataAccessInterface;
        this.loginPresenter = loginPresenter;
    }

    @Override
    public void execute(LoginInputData inputData) {
        // actual login logic goes here
        // Get username and password from input data.
        final String username = inputData.getUsername();
        final char[] password = inputData.getPassword();

        // if User does not exist...
        if (!userDataAccessObject.existsByUsername(username)) {
            loginPresenter.prepareFailView("Account does not exist.");
        }
        // if user exists... check if password is correct
        else {
            final User user = userDataAccessObject.getUser(username);
            final String correctPassword = user.getPassword();

            // if password from inputData is not correct...
            if (!PasswordHasher.matches(password, correctPassword)) {
                loginPresenter.prepareFailView("Incorrect password for \"" + username + "\".");
            }
            // if password from InputData is correct...
            else {
                // send presenter output data so it can update View
                final List<Integer> eventIds = loginEventDataAccessInterface.getEventIds(username);
                final List<EventSummary> eventSummaries;
                if (eventIds != null) {
                    eventSummaries = createEventSummaryListFromIds(eventIds);
                }
                else {
                    eventSummaries = new ArrayList<>();
                }
                final LoginOutputData loginOutputData = new LoginOutputData(
                        user.getUsername(),
                        user.getDisplayName(),
                        user.getPreferredCurrency(),
                        eventSummaries);
                loginPresenter.prepareSuccessView(loginOutputData);
            }
        }
    }

    private List<EventSummary> createEventSummaryListFromIds(List<Integer> eventIds) {
        final ArrayList<EventSummary> summaries = new ArrayList<>();
        for (final Integer eventId : eventIds) {
            try {
                summaries.add(createEventSummaryFromId(eventId));
            }
            catch (WhoOwesWhatDataAccessException exception) {
                throw new RuntimeException(exception);
            }
        }
        return summaries;
    }

    private EventSummary createEventSummaryFromId(Integer eventId)
            throws WhoOwesWhatDataAccessException {
        final Event event = loginEventDataAccessInterface.getEvent(eventId);

        return new EventSummary(eventId, event.getEventName(),
                event.getStartDate(), event.getEndDate());
    }
}

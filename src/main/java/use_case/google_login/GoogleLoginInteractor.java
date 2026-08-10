package use_case.google_login;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import data_access.PasswordHasher;
import entity.Event;
import entity.EventSummary;
import entity.User;
import entity.UserFactory;
import use_case.login.LoginDataAccessInterface;
import use_case.login.LoginEventDataAccessInterface;
import use_case.login.LoginOutputBoundary;
import use_case.login.LoginOutputData;
import use_case.signup.SignupDataAccessInterface;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/** 
 * Logs a user in with Google,
 * makes the account the first time then uses the normal login presenter.
*/
public class GoogleLoginInteractor implements GoogleLoginInputBoundary {

    private static final String DEFAULT_PREFERRED_CURRENCY = "CAD";
    private static final String NOT_AVAILABLE_MESSAGE =
            "Google sign-in was cancelled or is not set up.";

    private final GoogleAuthGateway authGateway;
    private final LoginDataAccessInterface userDataAccess;
    private final SignupDataAccessInterface signupDataAccess;
    private final LoginEventDataAccessInterface eventDataAccess;
    private final UserFactory userFactory;
    private final LoginOutputBoundary presenter;

    public GoogleLoginInteractor(GoogleAuthGateway authGateway,
                                 LoginDataAccessInterface userDataAccess,
                                 SignupDataAccessInterface signupDataAccess,
                                 LoginEventDataAccessInterface eventDataAccess,
                                 UserFactory userFactory,
                                 LoginOutputBoundary presenter) {
        this.authGateway = authGateway;
        this.userDataAccess = userDataAccess;
        this.signupDataAccess = signupDataAccess;
        this.eventDataAccess = eventDataAccess;
        this.userFactory = userFactory;
        this.presenter = presenter;
    }

    @Override
    public void execute() {
        final GoogleAccount account = this.authGateway.authenticate();
        if (account == null || account.getEmail() == null || account.getEmail().isBlank()) {
            this.presenter.prepareFailView(NOT_AVAILABLE_MESSAGE);
        }
        else {
            this.signIn(account);
        }
    }

    /**
     * Finds the account this Google user already has, creating one the first time they sign in.
     * The lookup goes by email rather than username so an account that has since been renamed is
     * still recognised instead of being duplicated.
     *
     * @param account the Google account that just signed in
     * @return the username the account is stored under
     */
    private String resolveUsername(GoogleAccount account) {
        final String renamedUsername = this.userDataAccess.findUsernameByEmail(account.getEmail());
        final String username;

        if (renamedUsername == null) {
            username = account.getEmail();
            if (!this.userDataAccess.existsByUsername(username)) {
                this.createAccount(account, username);
            }
        }
        else {
            username = renamedUsername;
        }

        return username;
    }

    private void createAccount(GoogleAccount account, String username) {
        final String displayName;

        if (account.getName() == null || account.getName().isBlank()) {
            displayName = username;
        }
        else {
            displayName = account.getName();
        }

        final String hashedPassword = PasswordHasher.hash(randomPassword());
        final User newUser = this.userFactory.create(
                username, displayName, account.getEmail(), hashedPassword,
                "", DEFAULT_PREFERRED_CURRENCY);

        this.signupDataAccess.save(newUser);
    }

    private void signIn(GoogleAccount account) {
        final String username = this.resolveUsername(account);

        final User user = this.userDataAccess.getUser(username);
        final List<Integer> eventIds = this.eventDataAccess.getEventIds(username);

        final List<EventSummary> summaries;
        if (eventIds == null) {
            summaries = new ArrayList<>();
        }
        else {
            summaries = this.summariesFor(eventIds);
        }

        this.presenter.prepareSuccessView(new LoginOutputData(
                user.getUsername(),
                user.getDisplayName(),
                user.getPreferredCurrency(),
                summaries));
    }

    private List<EventSummary> summariesFor(List<Integer> eventIds) {
        final List<EventSummary> summaries = new ArrayList<>();
        for (final Integer eventId : eventIds) {
            try {
                final Event event = this.eventDataAccess.getEvent(eventId);

                summaries.add(new EventSummary(eventId, event.getEventName(),
                        event.getStartDate(), event.getEndDate()));
            }
            catch (WhoOwesWhatDataAccessException exception) {
                throw new RuntimeException(exception);
            }
        }
        return summaries;
    }

    private static char[] randomPassword() {
        // Google accounts sign in through Google, so this local password is never used to log in.
        return (UUID.randomUUID().toString() + UUID.randomUUID()).toCharArray();
    }
}

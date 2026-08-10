package use_case.signup;

import java.util.regex.Pattern;

import data_access.PasswordHasher;
import entity.User;
import entity.UserFactory;

/**
 * The signup interactor.
 */
public final class SignupInteractor implements SignupInputBoundary {

    private static final int MAXIMUM_EMAIL_LENGTH = 254;
    private static final int MAXIMUM_LOCAL_PART_LENGTH = 64;
    private static final String REQUIRED_FIELDS_MESSAGE = "All fields are required.";
    private static final String INVALID_EMAIL_MESSAGE = "Please enter a valid email.";
    private static final String USER_EXISTS_MESSAGE = "User already exists.";
    private static final String EMAIL_PATTERN_TEXT =
            "^[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+"
                    + "(?:\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*"
                    + "@(?:[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?\\.)+"
                    + "[A-Za-z]{2,63}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_PATTERN_TEXT);
    private static final String DEFAULT_PREFERRED_CURRENCY = "CAD";

    private final SignupDataAccessInterface userDataAccessObject;
    private final SignupOutputBoundary userPresenter;
    private final UserFactory userFactory;

    /**
     * Creates a signup interactor.
     *
     * @param signupDataAccessInterface the signup data-access interface
     * @param signupOutputBoundary the signup output boundary
     * @param userFactory the user factory
     */
    public SignupInteractor(
            SignupDataAccessInterface signupDataAccessInterface,
            SignupOutputBoundary signupOutputBoundary,
            UserFactory userFactory) {

        this.userDataAccessObject = signupDataAccessInterface;
        this.userPresenter = signupOutputBoundary;
        this.userFactory = userFactory;
    }

    /**
     * Creates a user account using the supplied signup information.
     *
     * @param signupInputData the signup input data
     */
    @Override
    public void execute(SignupInputData signupInputData) {
        final String username = signupInputData.getUsername();
        final String displayName = signupInputData.getDisplayName();
        final String email = signupInputData.getEmail();
        final char[] password = signupInputData.getPassword();

        if (this.isBlank(username)
                || this.isBlank(displayName)
                || this.isBlank(email)
                || password.length == 0) {

            this.userPresenter.prepareFailView(REQUIRED_FIELDS_MESSAGE);
        }
        else if (!this.isValidEmail(email)) {
            this.userPresenter.prepareFailView(INVALID_EMAIL_MESSAGE);
        }
        else if (PasswordPolicy.getValidationError(password) != null) {
            this.userPresenter.prepareFailView(PasswordPolicy.getValidationError(password));
        }
        else if (this.userDataAccessObject.existsByName(username)) {
            this.userPresenter.prepareFailView(USER_EXISTS_MESSAGE);
        }
        else {
            final String hashedPassword = PasswordHasher.hash(password);
            final User user = this.userFactory.create(
                    username,
                    displayName,
                    email,
                    hashedPassword,
                    "",
                    DEFAULT_PREFERRED_CURRENCY
            );

            this.userDataAccessObject.save(user);

            final SignupOutputData signupOutputData =
                    new SignupOutputData(
                            user.getUsername(),
                            user.getDisplayName(),
                            false
                    );

            this.userPresenter.prepareSuccessView(signupOutputData);
        }
    }

    /**
     * Switches from the signup view to the login view.
     */
    @Override
    public void switchToLoginView() {
        this.userPresenter.switchToLoginView();
    }

    /**
     * Returns whether a value is null, empty, or whitespace.
     *
     * @param value the value to check
     * @return whether the value is blank
     */
    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Returns whether an email has a valid structure.
     *
     * @param email the email to validate
     * @return whether the email has a valid structure
     */
    private boolean isValidEmail(String email) {
        boolean validEmail = email.length() <= MAXIMUM_EMAIL_LENGTH;

        final int atSymbolIndex = email.indexOf('@');

        if (atSymbolIndex <= 0 || atSymbolIndex > MAXIMUM_LOCAL_PART_LENGTH) {
            validEmail = false;
        }
        else if (!EMAIL_PATTERN.matcher(email).matches()) {
            validEmail = false;
        }

        return validEmail;
    }
}

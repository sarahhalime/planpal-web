package use_case.logout;

/**
 * The interactor for the logout use case.
 */
public class LogoutInteractor implements LogoutInputBoundary {

    private final LogoutDataAccessInterface logoutDataAccessInterface;
    private final LogoutOutputBoundary logoutOutputBoundary;

    public LogoutInteractor(
            LogoutDataAccessInterface logoutDataAccessInterface,
            LogoutOutputBoundary logoutOutputBoundary) {
        this.logoutDataAccessInterface = logoutDataAccessInterface;
        this.logoutOutputBoundary = logoutOutputBoundary;
    }

    @Override
    public void execute() {
        logoutDataAccessInterface.clearCurrentUser();
        logoutOutputBoundary.prepareSuccessView();
    }
}

package use_case.logout;

/**
 * The data access interface for the logout use case.
 */
public interface LogoutDataAccessInterface {

    /**
     * Clears the currently logged-in user.
     */
    void clearCurrentUser();
}

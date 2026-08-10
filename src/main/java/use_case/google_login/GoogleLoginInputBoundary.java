package use_case.google_login;

/**
 * The input boundary for signing in with Google.
 */
public interface GoogleLoginInputBoundary {

    /**
     * Starts the Google sign-in flow, creating the account on first use and logging in.
     */
    void execute();
}

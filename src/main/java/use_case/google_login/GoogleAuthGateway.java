package use_case.google_login;

/** Runs the google sign in and gives back the account. */
public interface GoogleAuthGateway {

    /**
     * Does the actual google sign in with the OAuth.
     *
     * @return the account, or null if google isnt set up or got cancelled
     */
    GoogleAccount authenticate();
}

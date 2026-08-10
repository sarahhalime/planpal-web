package use_case.google_login;

/**
 * The identity returned by Google after a successful sign-in.
 */
public class GoogleAccount {

    private final String email;
    private final String name;

    public GoogleAccount(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }
}

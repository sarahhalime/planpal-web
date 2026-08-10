package use_case.signup;

/**
 * The input data for the signup use case.
 */
public class SignupInputData {

    private final String username;
    private final String displayName;
    private final String email;
    private final char[] password;

    public SignupInputData(String username, String displayName, String email, char[] password) {
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public char[] getPassword() {
        return password;
    }
}

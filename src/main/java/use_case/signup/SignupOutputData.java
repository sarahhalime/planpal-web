package use_case.signup;

/**
 * The output data for the signup use case.
 */
public class SignupOutputData {

    private final String username;
    private final String displayName;
    private final boolean useCaseFailed;

    public SignupOutputData(String username, String displayName, boolean useCaseFailed) {
        this.username = username;
        this.displayName = displayName;
        this.useCaseFailed = useCaseFailed;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isUseCaseFailed() {
        return useCaseFailed;
    }
}

package use_case.change_username;

public class ChangeUsernameOutputData {
    private final String username;
    private final boolean userCaseFailed;

    public ChangeUsernameOutputData(String username, boolean userCaseFailed) {
        this.username = username;
        this.userCaseFailed = userCaseFailed;
    }

    public String getUsername() {
        return username;
    }

    public boolean isUseCaseFailed() {
        return userCaseFailed;
    }
}

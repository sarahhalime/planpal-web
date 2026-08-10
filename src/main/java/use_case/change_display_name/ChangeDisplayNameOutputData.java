package use_case.change_display_name;

public class ChangeDisplayNameOutputData {
    private final String username;
    private final String displayName;
    private final boolean userCaseFailed;

    public ChangeDisplayNameOutputData(String username, String updatedDisplayName, boolean userCaseFailed) {
        this.username = username;
        this.displayName = updatedDisplayName;
        this.userCaseFailed = userCaseFailed;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isUseCaseFailed() {
        return userCaseFailed;
    }
}

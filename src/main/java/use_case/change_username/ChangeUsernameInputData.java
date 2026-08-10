package use_case.change_username;

public class ChangeUsernameInputData {

    private final String oldUsername;
    private final String newUsername;

    public ChangeUsernameInputData(String oldUsername, String newUsername) {
        this.oldUsername = oldUsername;
        this.newUsername = newUsername;
    }

    public String getOldUsername() {
        return oldUsername;
    }

    public String getNewUsername() {
        return newUsername;
    }
}

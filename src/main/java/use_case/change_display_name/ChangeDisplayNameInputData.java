package use_case.change_display_name;

/**
 * The input data for changing display names.
 */
public class ChangeDisplayNameInputData {

    private final String username;
    private final String displayName;

    public ChangeDisplayNameInputData(String username, String displayName) {
        this.username = username;
        this.displayName = displayName;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }
}

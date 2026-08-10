package use_case.view_profile;

/**
 * The input data for the view profile use case.
 */
public class ViewProfileInputData {

    private final String targetUsername;
    private final String currentUsername;

    public ViewProfileInputData(String targetUsername, String currentUsername) {
        this.targetUsername = targetUsername;
        this.currentUsername = currentUsername;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }
}

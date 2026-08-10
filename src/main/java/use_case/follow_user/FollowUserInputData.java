package use_case.follow_user;

/**
 * The input data for the follow user use case.
 */
public class FollowUserInputData {

    private final String currentUsername;
    private final String targetUsername;
    private final boolean follow;

    public FollowUserInputData(String currentUsername, String targetUsername, boolean follow) {
        this.currentUsername = currentUsername;
        this.targetUsername = targetUsername;
        this.follow = follow;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public boolean isFollow() {
        return follow;
    }
}

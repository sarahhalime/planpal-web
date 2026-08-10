package use_case.follow_user;

/**
 * The output data for the follow user use case.
 */
public class FollowUserOutputData {

    private final String targetUsername;
    private final boolean following;
    private final int followerCount;

    public FollowUserOutputData(String targetUsername, boolean following, int followerCount) {
        this.targetUsername = targetUsername;
        this.following = following;
        this.followerCount = followerCount;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public boolean isFollowing() {
        return following;
    }

    public int getFollowerCount() {
        return followerCount;
    }
}

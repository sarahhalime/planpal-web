package use_case.follow_user;

/**
 * The input boundary for the follow user use case.
 */
public interface FollowUserInputBoundary {

    /**
     * Follows or unfollows the target user.
     * @param followUserInputData the follow request
     */
    void execute(FollowUserInputData followUserInputData);
}

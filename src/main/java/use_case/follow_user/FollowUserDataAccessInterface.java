package use_case.follow_user;

/**
 * The data access interface for the follow user use case.
 */
public interface FollowUserDataAccessInterface {

    /**
     * Records that the follower now follows the followee.
     * @param follower the user doing the following
     * @param followee the user being followed
     */
    void follow(String follower, String followee);

    /**
     * Removes the follow relationship from follower to followee.
     * @param follower the user doing the unfollowing
     * @param followee the user being unfollowed
     */
    void unfollow(String follower, String followee);

    /**
     * Returns whether the follower currently follows the followee.
     * @param follower the potential follower
     * @param followee the potential followee
     * @return true if the follow relationship exists
     */
    boolean isFollowing(String follower, String followee);

    /**
     * Returns how many users follow the given user.
     * @param username the user whose followers are counted
     * @return the number of followers
     */
    int countFollowers(String username);

    /**
     * Returns how many users the given user follows.
     * @param username the user whose followees are counted
     * @return the number of users followed
     */
    int countFollowing(String username);
}

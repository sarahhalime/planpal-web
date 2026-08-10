package use_case.list_follows;

import java.util.List;

/**
 * Data access for reading a user's followers and following.
 */
public interface ListFollowsDataAccessInterface {

    /**
     * Returns the usernames that follow the given user.
     * @param username the user
     * @return the follower usernames
     */
    List<String> getFollowers(String username);

    /**
     * Returns the usernames that the given user follows.
     * @param username the user
     * @return the following usernames
     */
    List<String> getFollowing(String username);
}

package use_case.search_users;

import java.util.List;

/**
 * The data access interface for the search users use case.
 */
public interface SearchUsersDataAccessInterface {

    /**
     * Returns the usernames of every registered user.
     * @return all usernames
     */
    List<String> getAllUsernames();
}

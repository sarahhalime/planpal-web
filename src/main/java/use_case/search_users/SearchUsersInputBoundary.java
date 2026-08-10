package use_case.search_users;

/**
 * The input boundary for the search users use case.
 */
public interface SearchUsersInputBoundary {

    /**
     * Searches for users matching the query.
     * @param searchUsersInputData the search request
     */
    void execute(SearchUsersInputData searchUsersInputData);
}

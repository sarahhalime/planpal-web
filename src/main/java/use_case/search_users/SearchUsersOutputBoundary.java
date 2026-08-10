package use_case.search_users;

/**
 * The output boundary for the search users use case.
 */
public interface SearchUsersOutputBoundary {

    /**
     * Prepares the view showing the search results.
     * @param searchUsersOutputData the users that matched the query
     */
    void prepareSuccessView(SearchUsersOutputData searchUsersOutputData);
}

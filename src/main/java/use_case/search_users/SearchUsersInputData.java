package use_case.search_users;

/**
 * The input data for the search users use case.
 */
public class SearchUsersInputData {

    private final String query;
    private final String currentUsername;

    public SearchUsersInputData(String query, String currentUsername) {
        this.query = query;
        this.currentUsername = currentUsername;
    }

    public String getQuery() {
        return query;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }
}

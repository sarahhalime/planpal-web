package use_case.search_users;

import java.util.List;

/**
 * The output data for the search users use case.
 */
public class SearchUsersOutputData {

    private final String query;
    private final List<SearchedUser> results;

    public SearchUsersOutputData(String query, List<SearchedUser> results) {
        this.query = query;
        this.results = results;
    }

    public String getQuery() {
        return query;
    }

    public List<SearchedUser> getResults() {
        return results;
    }
}

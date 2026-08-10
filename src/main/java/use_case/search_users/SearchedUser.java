package use_case.search_users;

/**
 * A single user returned by a search, with whether the searcher already follows them.
 */
public class SearchedUser {

    private final String username;
    private final boolean following;

    public SearchedUser(String username, boolean following) {
        this.username = username;
        this.following = following;
    }

    public String getUsername() {
        return username;
    }

    public boolean isFollowing() {
        return following;
    }
}

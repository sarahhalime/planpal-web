package use_case.list_follows;

/**
 * Input data for the list follows use case.
 */
public class ListFollowsInputData {

    private final String username;
    private final boolean followers;

    /**
     * Creates the input data.
     * @param username the user whose connections are listed
     * @param followers true to list followers, false to list who the user follows
     */
    public ListFollowsInputData(String username, boolean followers) {
        this.username = username;
        this.followers = followers;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean isFollowers() {
        return this.followers;
    }
}

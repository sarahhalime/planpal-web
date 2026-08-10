package use_case.list_follows;

import java.util.List;

/**
 * Output data for the list follows use case.
 */
public class ListFollowsOutputData {

    private final boolean followers;
    private final List<String> usernames;

    /**
     * Creates the output data.
     * @param followers true if this is a followers list, false for a following list
     * @param usernames the usernames in the list
     */
    public ListFollowsOutputData(boolean followers, List<String> usernames) {
        this.followers = followers;
        this.usernames = usernames;
    }

    public boolean isFollowers() {
        return this.followers;
    }

    public List<String> getUsernames() {
        return this.usernames;
    }
}

package use_case.list_follows;

import java.util.List;

/**
 * The interactor for the list follows use case.
 */
public class ListFollowsInteractor implements ListFollowsInputBoundary {

    private final ListFollowsDataAccessInterface dataAccess;
    private final ListFollowsOutputBoundary presenter;

    public ListFollowsInteractor(ListFollowsDataAccessInterface dataAccess,
                                 ListFollowsOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(ListFollowsInputData listFollowsInputData) {
        final String username = listFollowsInputData.getUsername();
        final boolean followers = listFollowsInputData.isFollowers();
        final List<String> usernames;
        if (followers) {
            usernames = dataAccess.getFollowers(username);
        }
        else {
            usernames = dataAccess.getFollowing(username);
        }
        presenter.prepareSuccessView(new ListFollowsOutputData(followers, usernames));
    }
}

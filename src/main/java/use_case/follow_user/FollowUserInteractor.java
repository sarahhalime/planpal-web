package use_case.follow_user;

/**
 * The interactor for the follow user use case.
 */
public class FollowUserInteractor implements FollowUserInputBoundary {

    private final FollowUserDataAccessInterface dataAccess;
    private final FollowUserOutputBoundary presenter;

    public FollowUserInteractor(FollowUserDataAccessInterface dataAccess,
                                FollowUserOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(FollowUserInputData followUserInputData) {
        final String currentUsername = followUserInputData.getCurrentUsername();
        final String targetUsername = followUserInputData.getTargetUsername();

        if (currentUsername == null || targetUsername == null
                || currentUsername.isBlank() || targetUsername.isBlank()) {
            presenter.prepareFailView("A user is required to follow.");
        }
        else if (currentUsername.equals(targetUsername)) {
            presenter.prepareFailView("You cannot follow yourself.");
        }
        else {
            if (followUserInputData.isFollow()) {
                dataAccess.follow(currentUsername, targetUsername);
            }
            else {
                dataAccess.unfollow(currentUsername, targetUsername);
            }

            final FollowUserOutputData followUserOutputData = new FollowUserOutputData(
                    targetUsername,
                    dataAccess.isFollowing(currentUsername, targetUsername),
                    dataAccess.countFollowers(targetUsername));
            presenter.prepareSuccessView(followUserOutputData);
        }
    }
}

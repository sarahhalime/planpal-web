package use_case.view_profile;

import use_case.follow_user.FollowUserDataAccessInterface;

/**
 * The interactor for the view profile use case.
 */
public class ViewProfileInteractor implements ViewProfileInputBoundary {

    private final ViewProfileDataAccessInterface profileDataAccess;
    private final FollowUserDataAccessInterface followDataAccess;
    private final ViewProfileOutputBoundary presenter;

    public ViewProfileInteractor(ViewProfileDataAccessInterface profileDataAccess,
                                 FollowUserDataAccessInterface followDataAccess,
                                 ViewProfileOutputBoundary presenter) {
        this.profileDataAccess = profileDataAccess;
        this.followDataAccess = followDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(ViewProfileInputData viewProfileInputData) {
        final String targetUsername = viewProfileInputData.getTargetUsername();
        final String currentUsername = viewProfileInputData.getCurrentUsername();
        final boolean ownProfile = targetUsername.equals(currentUsername);

        final ViewProfileOutputData viewProfileOutputData = new ViewProfileOutputData(
                targetUsername,
                profileDataAccess.getBio(targetUsername),
                followDataAccess.countFollowers(targetUsername),
                followDataAccess.countFollowing(targetUsername),
                !ownProfile && followDataAccess.isFollowing(currentUsername, targetUsername),
                ownProfile,
                profileDataAccess.getProfilePicture(targetUsername));
        presenter.prepareSuccessView(viewProfileOutputData);
    }
}

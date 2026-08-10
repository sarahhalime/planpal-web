package use_case.update_profile;

/**
 * The interactor for the update profile use case.
 */
public class UpdateProfileInteractor implements UpdateProfileInputBoundary {

    private static final int MAX_BIO_LENGTH = 300;

    private final UpdateProfileDataAccessInterface dataAccess;
    private final UpdateProfileOutputBoundary presenter;

    public UpdateProfileInteractor(UpdateProfileDataAccessInterface dataAccess,
                                   UpdateProfileOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(UpdateProfileInputData updateProfileInputData) {
        final String username = updateProfileInputData.getUsername();
        final String bio;
        if (updateProfileInputData.getBio() == null) {
            bio = "";
        }
        else {
            bio = updateProfileInputData.getBio().trim();
        }

        if (username == null || username.isBlank()) {
            presenter.prepareFailView("You must be signed in to edit your profile.");
        }
        else if (bio.length() > MAX_BIO_LENGTH) {
            presenter.prepareFailView("Your bio must be " + MAX_BIO_LENGTH + " characters or fewer.");
        }
        else {
            dataAccess.setBio(username, bio);
            if (updateProfileInputData.isClearPicture()) {
                dataAccess.setProfilePicture(username, null);
            }
            else if (updateProfileInputData.getProfilePicture() != null) {
                dataAccess.setProfilePicture(username, updateProfileInputData.getProfilePicture());
            }
            presenter.prepareSuccessView(new UpdateProfileOutputData(username, bio));
        }
    }
}

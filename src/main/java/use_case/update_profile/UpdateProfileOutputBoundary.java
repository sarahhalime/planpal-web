package use_case.update_profile;

/**
 * The output boundary for the update profile use case.
 */
public interface UpdateProfileOutputBoundary {

    /**
     * Prepares the view after the profile is updated.
     * @param updateProfileOutputData the updated profile details
     */
    void prepareSuccessView(UpdateProfileOutputData updateProfileOutputData);

    /**
     * Prepares the failure view for the update profile use case.
     * @param errorMessage the reason the update failed
     */
    void prepareFailView(String errorMessage);
}

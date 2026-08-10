package use_case.update_profile;

/**
 * The input boundary for the update profile use case.
 */
public interface UpdateProfileInputBoundary {

    /**
     * Updates the current user's profile details.
     * @param updateProfileInputData the new profile details
     */
    void execute(UpdateProfileInputData updateProfileInputData);
}

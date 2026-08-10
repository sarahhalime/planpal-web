package use_case.view_profile;

/**
 * The input boundary for the view profile use case.
 */
public interface ViewProfileInputBoundary {

    /**
     * Loads the profile of the target user.
     * @param viewProfileInputData the profile request
     */
    void execute(ViewProfileInputData viewProfileInputData);
}

package use_case.view_profile;

/**
 * The output boundary for the view profile use case.
 */
public interface ViewProfileOutputBoundary {

    /**
     * Prepares the view showing the requested profile.
     * @param viewProfileOutputData the loaded profile
     */
    void prepareSuccessView(ViewProfileOutputData viewProfileOutputData);
}

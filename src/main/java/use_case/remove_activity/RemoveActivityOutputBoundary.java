package use_case.remove_activity;

/**
 * Defines the output boundary for removing an activity.
 */
public interface RemoveActivityOutputBoundary {

    /**
     * Prepares the view after an activity is removed successfully.
     *
     * @param outputData the removed activity data
     */
    void prepareSuccessView(RemoveActivityOutputData outputData);

    /**
     * Prepares the view after removal fails.
     *
     * @param errorMessage the failure message
     */
    void prepareFailView(String errorMessage);
}

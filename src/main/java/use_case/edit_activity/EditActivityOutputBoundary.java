package use_case.edit_activity;

/**
 * Defines the output boundary for editing an activity.
 */
public interface EditActivityOutputBoundary {

    /**
     * Prepares the view after an activity is edited successfully.
     *
     * @param outputData the edited activity data
     */
    void prepareSuccessView(EditActivityOutputData outputData);

    /**
     * Prepares the view after editing fails.
     *
     * @param errorMessage the failure message
     */
    void prepareFailView(String errorMessage);
}

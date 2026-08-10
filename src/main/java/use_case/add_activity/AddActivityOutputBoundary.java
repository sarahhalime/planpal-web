package use_case.add_activity;

/**
 * Defines the output boundary for the add-activity use case.
 */
public interface AddActivityOutputBoundary {

    /**
     * Prepares the view after an activity is added successfully.
     *
     * @param outputData the added activity data
     */
    void prepareSuccessView(AddActivityOutputData outputData);

    /**
     * Prepares the view after the use case fails.
     *
     * @param errorMessage the failure message
     */
    void prepareFailView(String errorMessage);
}

package use_case.edit_activity;

/**
 * Defines the input boundary for editing an activity.
 */
public interface EditActivityInputBoundary {

    /**
     * Edits an activity using the supplied input data.
     *
     * @param inputData the edit-activity input data
     */
    void execute(EditActivityInputData inputData);
}

package use_case.remove_activity;

/**
 * Defines the input boundary for removing an activity.
 */
public interface RemoveActivityInputBoundary {

    /**
     * Removes an activity using the supplied input data.
     *
     * @param inputData the remove-activity input data
     */
    void execute(RemoveActivityInputData inputData);
}

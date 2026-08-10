package use_case.add_activity;

/**
 * Defines the input boundary for adding an activity to an event.
 */
public interface AddActivityInputBoundary {

    /**
     * Adds an activity using the supplied input data.
     *
     * @param inputData the add-activity input data
     */
    void execute(AddActivityInputData inputData);
}

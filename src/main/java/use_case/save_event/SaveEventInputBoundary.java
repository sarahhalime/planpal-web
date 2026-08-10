package use_case.save_event;

/**
 * The input boundary for the save event use case.
 */
public interface SaveEventInputBoundary {

    /**
     * Executes the save event use case.
     * @param saveEventInputData the information needed to save the event
     */
    void execute(SaveEventInputData saveEventInputData);
}

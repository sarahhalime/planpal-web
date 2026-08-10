package use_case.delete_event;

/**
 * The input boundary for the delete event use case.
 */
public interface DeleteEventInputBoundary {

    /**
     * Executes the delete event use case.
     *
     * @param deleteEventInputData the information needed to delete the event
     */
    void execute(DeleteEventInputData deleteEventInputData);
}

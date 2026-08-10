package use_case.edit_event;

/**
 * The input boundary for the edit event use case.
 */
public interface EditEventInputBoundary {

    /**
     * Executes the edit event use case.
     *
     * @param editEventInputData the information needed to edit the event
     */
    void execute(EditEventInputData editEventInputData);
}

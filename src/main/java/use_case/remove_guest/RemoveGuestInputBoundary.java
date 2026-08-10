package use_case.remove_guest;

/**
 * The input boundary for actions which are related to removing a guest (attendee).
 */
public interface RemoveGuestInputBoundary {

    /**
     * This method executes the remove guest use case.
     * @param removeGuestInputData the input data
     */
    void execute(RemoveGuestInputData removeGuestInputData);
}

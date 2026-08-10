package use_case.view_attendees;

/**
 * Input boundary for viewing the attendees of an event.
 */
public interface ViewAttendeesInputBoundary {

    /**
     * Loads the attendees for the selected event.
     *
     * @param inputData the selected event identifier
     */
    void execute(ViewAttendeesInputData inputData);
}

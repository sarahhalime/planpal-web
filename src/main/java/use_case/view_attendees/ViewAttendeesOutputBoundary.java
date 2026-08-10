package use_case.view_attendees;

/**
 * Output boundary for viewing event attendees.
 */
public interface ViewAttendeesOutputBoundary {

    /**
     * Presents the loaded attendees.
     *
     * @param outputData attendee data
     */
    void prepareSuccessView(ViewAttendeesOutputData outputData);

    /**
     * Presents a failure message.
     *
     * @param errorMessage the failure message
     */
    void prepareFailView(String errorMessage);
}

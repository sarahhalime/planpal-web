package use_case.view_attendees;

import java.util.List;

/**
 * Output data containing all attendees for an event.
 */
public final class ViewAttendeesOutputData {
    private final List<ViewAttendeeData> attendees;

    /**
     * Creates attendee output data.
     *
     * @param attendees the attendees to display
     */
    public ViewAttendeesOutputData(List<ViewAttendeeData> attendees) {
        this.attendees = List.copyOf(attendees);
    }

    /**
     * Returns the attendees.
     *
     * @return the attendee list
     */
    public List<ViewAttendeeData> getAttendees() {
        return this.attendees;
    }
}

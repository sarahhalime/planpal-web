package use_case.edit_event;

import entity.Event;

/**
 * The output data for the edit event use case.
 */
public class EditEventOutputData {
    private final Event event;
    private final int eventId;
    private final String eventName;

    public EditEventOutputData(
            Event event,
            int eventId,
            String eventName
    ) {
        this.event = event;
        this.eventId = eventId;
        this.eventName = eventName;
    }

    public Event getEvent() {
        return this.event;
    }

    public int getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }
}

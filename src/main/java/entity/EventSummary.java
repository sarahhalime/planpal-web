package entity;

/**
 * The short form of an event shown in the sidebar list.
 */
public class EventSummary {
    private final int eventId;
    private final String eventName;
    private final String dateTimeInfo;
    private final String endDateInfo;

    /**
     * Creates a summary for an event with no end date.
     *
     * @param eventId the event identifier
     * @param eventName the event name
     * @param dateTimeInfo the event start date
     */
    public EventSummary(int eventId, String eventName, String dateTimeInfo) {
        this(eventId, eventName, dateTimeInfo, null);
    }

    /**
     * Creates a summary for an event.
     *
     * @param eventId the event identifier
     * @param eventName the event name
     * @param dateTimeInfo the event start date
     * @param endDateInfo the event end date, or null when it has none
     */
    public EventSummary(int eventId, String eventName, String dateTimeInfo, String endDateInfo) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.dateTimeInfo = dateTimeInfo;
        this.endDateInfo = endDateInfo;
    }

    public int getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getDateTimeInfo() {
        return dateTimeInfo;
    }

    /**
     * The event end date.
     *
     * @return the end date, or null when the event has none
     */
    public String getEndDateInfo() {
        return endDateInfo;
    }
}

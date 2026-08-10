package use_case.select_event;

import entity.EventScheduleInput;

/**
 * The descriptive fields of the selected event.
 */
public final class SelectEventDetails {

    private static final String DEFAULT_CURRENCY = "CAD";

    private final int eventId;
    private final String eventName;
    private final String eventDescription;
    private final String eventLocation;
    private final String eventCurrency;
    private final String startDate;
    private final String startTime;
    private final String endDate;
    private final String endTime;

    /**
     * Creates the descriptive part of an event that has no end date or time.
     *
     * @param eventId the event identifier
     * @param eventName the event name
     * @param eventDescription the event description
     * @param eventLocation the event location
     * @param startDate the event start date
     * @param startTime the event start time
     */
    public SelectEventDetails(int eventId, String eventName, String eventDescription,
                              String eventLocation, String startDate, String startTime) {
        this(eventId, eventName, eventDescription, eventLocation, DEFAULT_CURRENCY,
                new EventScheduleInput(startDate, startTime, null, null));
    }

    /**
     * Creates the descriptive part of the selected event.
     *
     * @param eventId the event identifier
     * @param eventName the event name
     * @param eventDescription the event description
     * @param eventLocation the event location
     * @param eventCurrency the event currency code
     * @param schedule when the event starts and ends
     */
    public SelectEventDetails(int eventId, String eventName, String eventDescription,
                              String eventLocation, String eventCurrency,
                              EventScheduleInput schedule) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventLocation = eventLocation;
        this.eventCurrency = eventCurrency;
        this.startDate = schedule.getStartDate();
        this.startTime = schedule.getStartTime();
        this.endDate = schedule.getEndDate();
        this.endTime = schedule.getEndTime();
    }

    /**
     * The event currency.
     * @return the event currency code
     */
    public String getEventCurrency() {
        return this.eventCurrency;
    }

    /**
     * The event end date.
     * @return the end date, or null when the event has none
     */
    public String getEndDate() {
        return this.endDate;
    }

    /**
     * The event end time.
     * @return the end time, or null when the event has none
     */
    public String getEndTime() {
        return this.endTime;
    }

    /**
     * The event identifier.
     * @return the event id
     */
    public int getEventId() {
        return this.eventId;
    }

    /**
     * The event name.
     * @return the event name
     */
    public String getEventName() {
        return this.eventName;
    }

    /**
     * The event description.
     * @return the event description
     */
    public String getEventDescription() {
        return this.eventDescription;
    }

    /**
     * The event location.
     * @return the event location
     */
    public String getEventLocation() {
        return this.eventLocation;
    }

    /**
     * The event start date.
     * @return the start date
     */
    public String getStartDate() {
        return this.startDate;
    }

    /**
     * The event start time.
     * @return the start time
     */
    public String getStartTime() {
        return this.startTime;
    }
}

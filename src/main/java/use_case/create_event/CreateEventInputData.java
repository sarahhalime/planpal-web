package use_case.create_event;

import entity.EventScheduleInput;

/**
 * The input data for the create event use case.
 */
public class CreateEventInputData {

    private final String username;
    private final String eventName;
    private final String eventDescription;
    private final String eventLocation;
    private final Double eventBudget;
    private final String startDate;
    private final String startTime;
    private final String endDate;
    private final String endTime;
    private final String eventCurrency;

    public CreateEventInputData(String username, String eventName, String eventDescription,
                                String eventLocation, Double eventBudget,
                                String eventCurrency, EventScheduleInput schedule) {
        this.username = username;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventLocation = eventLocation;
        this.eventBudget = eventBudget;
        this.eventCurrency = eventCurrency;
        this.startDate = schedule.getStartDate();
        this.startTime = schedule.getStartTime();
        this.endDate = schedule.getEndDate();
        this.endTime = schedule.getEndTime();
    }

    /**
     * Returns the name of the event.
     *
     * @return the event name
     */
    public String getEventName() {
        return eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public Double getEventBudget() {
        return eventBudget;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getUsername() {
        return username;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public String getEndTime() {
        return this.endTime;
    }

    /**
     * Returns the event currency.
     *
     * @return the event currency code
     */
    public String getEventCurrency() {
        return this.eventCurrency;
    }
}

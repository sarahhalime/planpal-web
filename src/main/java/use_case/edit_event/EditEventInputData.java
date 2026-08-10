package use_case.edit_event;

import entity.EventScheduleInput;

/**
 * The input data for the edit event use case.
 */
public class EditEventInputData {
    private final int eventId;
    private final String eventName;
    private final String eventDescription;
    private final String eventLocation;
    private final Double eventBudget;
    private final String startDate;
    private final String startTime;
    private final String endDate;
    private final String endTime;

    public EditEventInputData(int eventId, String eventName, String eventDescription,
                              String eventLocation, Double eventBudget,
                              EventScheduleInput schedule) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventLocation = eventLocation;
        this.eventBudget = eventBudget;
        this.startDate = schedule.getStartDate();
        this.startTime = schedule.getStartTime();
        this.endDate = schedule.getEndDate();
        this.endTime = schedule.getEndTime();
    }

    public int getEventId() {
        return eventId;
    }

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

    public String getEndDate() {
        return this.endDate;
    }

    public String getEndTime() {
        return this.endTime;
    }
}

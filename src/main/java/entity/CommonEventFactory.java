package entity;

import java.util.List;

/**
 * Creates default event entities.
 */
public final class CommonEventFactory implements EventFactory {

    /**
     * Creates a common event with its scheduled activities.
     *
     * @param eventId the event identifier
     * @param details the event's descriptive fields
     * @param eventSchedule the event schedule
     * @param attendeeList the attendee usernames
     * @param expenseList the event expenses
     * @param activityList the event activities
     * @return the created event
     */
    @Override
    public Event createEvent(
            int eventId,
            EventDetails details,
            EventSchedule eventSchedule,
            List<String> attendeeList,
            List<Expense> expenseList,
            List<Activity> activityList) {

        return new CommonEvent(eventId, details, eventSchedule, attendeeList,
                expenseList, activityList);
    }

}

package entity;

import java.util.List;

/**
 * Creates event entities.
 */
public interface EventFactory {

    /**
     * Creates an event without activities.
     *
     * @param eventId the event identifier
     * @param details the event's descriptive fields
     * @param eventSchedule the event schedule
     * @param attendeeList the attendee usernames
     * @param expenseList the event expenses
     * @return the created event
     */
    default Event createEvent(
            int eventId,
            EventDetails details,
            EventSchedule eventSchedule,
            List<String> attendeeList,
            List<Expense> expenseList) {

        return this.createEvent(eventId, details, eventSchedule, attendeeList,
                expenseList, List.of());
    }

    /**
     * Creates an event with its scheduled activities.
     *
     * @param eventId the event identifier
     * @param details the event's descriptive fields
     * @param eventSchedule the event schedule
     * @param attendeeList the attendee usernames
     * @param expenseList the event expenses
     * @param activityList the event activities
     * @return the created event
     */
    Event createEvent(
            int eventId,
            EventDetails details,
            EventSchedule eventSchedule,
            List<String> attendeeList,
            List<Expense> expenseList,
            List<Activity> activityList);
}

package entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Represents an event managed by PlanPal.
 */
public interface Event {

    /**
     * Returns the event name.
     *
     * @return the event name
     */
    String getEventName();

    /**
     * Returns the event description.
     *
     * @return the event description
     */
    String getEventDescription();

    /**
     * Returns the event budget.
     *
     * @return the event budget
     */
    Double getEventBudget();

    /**
     * Returns the currency used for this event.
     *
     * @return the event currency code
     */
    default String getEventCurrency() {
        return "CAD";
    }

    /**
     * Returns the event location.
     *
     * @return the event location
     */
    String getEventLocation();

    /**
     * Returns the event schedule.
     *
     * @return the event schedule
     */
    EventSchedule getEventSchedule();

    /**
     * Returns the event start date.
     *
     * @return the event start date
     */
    default String getStartDate() {
        return this.getEventSchedule().getStartDate().toString();
    }

    /**
     * Returns the event start time.
     *
     * @return the event start time
     */
    default String getStartTime() {
        return this.getEventSchedule().getStartTime().toString();
    }

    /**
     * Returns the event end date, which is optional.
     *
     * @return the event end date, or null when the event has no end date
     */
    default String getEndDate() {
        final LocalDate endDate = this.getEventSchedule().getEndDate();
        String result = null;

        if (endDate != null) {
            result = endDate.toString();
        }

        return result;
    }

    /**
     * Returns the event end time, which is optional.
     *
     * @return the event end time, or null when the event has no end time
     */
    default String getEndTime() {
        final LocalTime endTime = this.getEventSchedule().getEndTime();
        String result = null;

        if (endTime != null) {
            result = endTime.toString();
        }

        return result;
    }

    /**
     * Returns the event identifier.
     *
     * @return the event identifier
     */
    int getEventId();

    /**
     * Returns the usernames of the registered users attending the event.
     *
     * @return the attendee usernames
     */
    List<String> getAttendeeUsernames();

    /**
     * Returns the expenses associated with the event.
     *
     * @return the event expenses
     */
    List<Expense> getExpenseList();

    /**
     * Returns the activities scheduled for the event.
     * The default implementation keeps existing implementations of
     * Event compatible with the activity feature.
     *
     * @return the event activities
     */
    default List<Activity> getActivityList() {
        return List.of();
    }
}

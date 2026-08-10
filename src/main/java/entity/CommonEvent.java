package entity;

import java.util.List;

/**
 * Represents the default implementation of a PlanPal event.
 */
public final class CommonEvent implements Event {

    private final int eventId;
    private final String eventName;
    private final String eventDescription;
    private final String eventLocation;
    private final Double eventBudget;
    private final String eventCurrency;
    private final EventSchedule eventSchedule;
    private final List<String> attendeeUsernames;
    private final List<Expense> expenseList;
    private final List<Activity> activityList;

    /**
     * Constructs a common event without activities.
     *
     * @param eventId the event identifier
     * @param details the event's descriptive fields
     * @param eventSchedule the event schedule
     * @param attendeeUsernames the attendee usernames
     * @param expenseList the event expenses
     */
    public CommonEvent(
            int eventId,
            EventDetails details,
            EventSchedule eventSchedule,
            List<String> attendeeUsernames,
            List<Expense> expenseList) {

        this(eventId, details, eventSchedule, attendeeUsernames, expenseList, List.of());
    }

    /**
     * Constructs a common event with its scheduled activities.
     *
     * @param eventId the event identifier
     * @param details the event's descriptive fields
     * @param eventSchedule the event schedule
     * @param attendeeUsernames the attendee usernames
     * @param expenseList the event expenses
     * @param activityList the event activities
     */
    public CommonEvent(
            int eventId,
            EventDetails details,
            EventSchedule eventSchedule,
            List<String> attendeeUsernames,
            List<Expense> expenseList,
            List<Activity> activityList) {

        this.eventId = eventId;
        this.eventName = details.getEventName();
        this.eventDescription = details.getEventDescription();
        this.eventLocation = details.getEventLocation();
        this.eventBudget = details.getEventBudget();
        this.eventCurrency = details.getEventCurrency();
        this.eventSchedule = eventSchedule;
        this.attendeeUsernames = attendeeUsernames;
        this.expenseList = expenseList;
        this.activityList = activityList;
    }

    /**
     * Returns the event identifier.
     *
     * @return the event identifier
     */
    @Override
    public int getEventId() {
        return this.eventId;
    }

    /**
     * Returns the event name.
     *
     * @return the event name
     */
    @Override
    public String getEventName() {
        return this.eventName;
    }

    /**
     * Returns the event description.
     *
     * @return the event description
     */
    @Override
    public String getEventDescription() {
        return this.eventDescription;
    }

    /**
     * Returns the event location.
     *
     * @return the event location
     */
    @Override
    public String getEventLocation() {
        return this.eventLocation;
    }

    /**
     * Returns the currency used for this event.
     *
     * @return the event currency code
     */
    @Override
    public String getEventCurrency() {
        return this.eventCurrency;
    }

    /**
     * Returns the event budget.
     *
     * @return the event budget
     */
    @Override
    public Double getEventBudget() {
        return this.eventBudget;
    }

    /**
     * Returns the event schedule.
     *
     * @return the event schedule
     */
    @Override
    public EventSchedule getEventSchedule() {
        return this.eventSchedule;
    }

    /**
     * Returns the usernames of the registered users attending the event.
     *
     * @return the attendee usernames
     */
    @Override
    public List<String> getAttendeeUsernames() {
        return this.attendeeUsernames;
    }

    /**
     * Returns the expenses associated with the event.
     *
     * @return the event expenses
     */
    @Override
    public List<Expense> getExpenseList() {
        return this.expenseList;
    }

    /**
     * Returns the activities scheduled for the event.
     *
     * @return the scheduled activities
     */
    @Override
    public List<Activity> getActivityList() {
        return this.activityList;
    }
}

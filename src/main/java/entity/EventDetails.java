package entity;

/**
 * The descriptive fields of an event: what it is called, what it is for, where it happens,
 * and what it is allowed to cost.
 */
public final class EventDetails {

    private final String eventName;
    private final String eventDescription;
    private final String eventLocation;
    private final Double eventBudget;
    private final String eventCurrency;

    /**
     * Creates the descriptive fields of an event.
     *
     * @param eventName the event name
     * @param eventDescription the event description
     * @param eventLocation the event location
     * @param eventBudget the event budget, or null when there is none
     * @param eventCurrency the currency the budget is kept in
     */
    public EventDetails(String eventName, String eventDescription,
                        String eventLocation, Double eventBudget, String eventCurrency) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventLocation = eventLocation;
        this.eventBudget = eventBudget;
        this.eventCurrency = eventCurrency;
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
     * The event budget, or null when there is none.
     * @return the event budget
     */
    public Double getEventBudget() {
        return this.eventBudget;
    }

    /**
     * The currency the budget is kept in.
     * @return the event currency code
     */
    public String getEventCurrency() {
        return this.eventCurrency;
    }
}

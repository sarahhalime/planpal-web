package use_case.select_event;

import java.util.List;

/**
 * Contains the information returned by the select-event use case.
 */
public final class SelectEventOutputData {

    private final int eventId;
    private final String eventName;
    private final String eventDescription;
    private final String eventLocation;
    private final String eventCurrency;
    private final String startDate;
    private final String startTime;
    private final String endDate;
    private final String endTime;
    private final double totalBudget;
    private final double totalSpent;
    private final double unsettledDebts;
    private final int expenseCount;
    private final int peopleOweCount;
    private final List<SelectEventExpenseData> expenses;
    private final List<SelectEventActivityData> activities;
    private final byte[] eventPhoto;

    /**
     * Creates selected-event output data with activities and an event photo.
     *
     * @param details the event details
     * @param totals the event's budget details
     * @param expenses the expense rows
     * @param activities the activity rows
     * @param eventPhoto event-photo bytes, or null
     */
    public SelectEventOutputData(
            SelectEventDetails details,
            SelectEventTotals totals,
            List<SelectEventExpenseData> expenses,
            List<SelectEventActivityData> activities,
            byte[] eventPhoto) {

        this.eventId = details.getEventId();
        this.eventName = details.getEventName();
        this.eventDescription = details.getEventDescription();
        this.eventLocation = details.getEventLocation();
        this.eventCurrency = details.getEventCurrency();
        this.startDate = details.getStartDate();
        this.startTime = details.getStartTime();
        this.endDate = details.getEndDate();
        this.endTime = details.getEndTime();
        this.totalBudget = totals.getTotalBudget();
        this.totalSpent = totals.getTotalSpent();
        this.unsettledDebts = totals.getUnsettledDebts();
        this.expenseCount = totals.getExpenseCount();
        this.peopleOweCount = totals.getPeopleOweCount();
        this.expenses = List.copyOf(expenses);
        this.activities = List.copyOf(activities);
        if (eventPhoto == null) {
            this.eventPhoto = null;
        }
        else {
            this.eventPhoto = eventPhoto.clone();
        }
    }

    /**
     * Returns the event identifier.
     *
     * @return the event identifier
     */
    public int getEventId() {
        return this.eventId;
    }

    /**
     * Returns the event name.
     *
     * @return the event name
     */
    public String getEventName() {
        return this.eventName;
    }

    /**
     * Returns the event description.
     *
     * @return the event description
     */
    public String getEventDescription() {
        return this.eventDescription;
    }

    /**
     * Returns the event location.
     *
     * @return the event location
     */
    public String getEventLocation() {
        return this.eventLocation;
    }

    /**
     * Returns the source currency used for event calculations.
     *
     * @return event currency code
     */
    public String getEventCurrency() {
        return this.eventCurrency;
    }

    /**
     * Returns the event start date.
     *
     * @return the event start date
     */
    public String getStartDate() {
        return this.startDate;
    }

    /**
     * Returns the event start time.
     *
     * @return the event start time
     */
    public String getStartTime() {
        return this.startTime;
    }

    /**
     * Returns the event end date.
     *
     * @return the event end date, or null when the event has none
     */
    public String getEndDate() {
        return this.endDate;
    }

    /**
     * Returns the event end time.
     *
     * @return the event end time, or null when the event has none
     */
    public String getEndTime() {
        return this.endTime;
    }

    /**
     * Returns the total event budget.
     *
     * @return the total event budget
     */
    public double getTotalBudget() {
        return this.totalBudget;
    }

    /**
     * Returns the total amount spent.
     *
     * @return the total amount spent
     */
    public double getTotalSpent() {
        return this.totalSpent;
    }

    /**
     * Returns the total unsettled debt.
     *
     * @return the total unsettled debt
     */
    public double getUnsettledDebts() {
        return this.unsettledDebts;
    }

    /**
     * Returns the number of expenses.
     *
     * @return the expense count
     */
    public int getExpenseCount() {
        return this.expenseCount;
    }

    /**
     * Returns the number of people who owe money.
     *
     * @return the number of people who owe money
     */
    public int getPeopleOweCount() {
        return this.peopleOweCount;
    }

    /**
     * Returns the expense rows.
     *
     * @return the expense rows
     */
    public List<SelectEventExpenseData> getExpenses() {
        return this.expenses;
    }

    /**
     * Returns the activity rows.
     *
     * @return the activity rows
     */
    public List<SelectEventActivityData> getActivities() {
        return this.activities;
    }

    /**
     * Returns a copy of the event photo.
     *
     * @return event-photo bytes, or null when no photo is stored
     */
    public byte[] getEventPhoto() {
        final byte[] result;
        if (this.eventPhoto == null) {
            result = null;
        }
        else {
            result = this.eventPhoto.clone();
        }
        return result;
    }
}

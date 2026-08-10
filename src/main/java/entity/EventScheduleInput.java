package entity;

/**
 * The four schedule fields as typed into the create and edit event forms.
 */
public final class EventScheduleInput {

    private final String startDate;
    private final String startTime;
    private final String endDate;
    private final String endTime;

    /**
     * Creates the typed schedule fields.
     *
     * @param startDate the start date text
     * @param startTime the start time text
     * @param endDate the end date text, or null
     * @param endTime the end time text, or null
     */
    public EventScheduleInput(String startDate, String startTime, String endDate, String endTime) {
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
    }

    /**
     * The start date text.
     * @return the start date
     */
    public String getStartDate() {
        return this.startDate;
    }

    /**
     * The start time text.
     * @return the start time
     */
    public String getStartTime() {
        return this.startTime;
    }

    /**
     * The end date text, or null.
     * @return the end date
     */
    public String getEndDate() {
        return this.endDate;
    }

    /**
     * The end time text, or null.
     * @return the end time
     */
    public String getEndTime() {
        return this.endTime;
    }
}

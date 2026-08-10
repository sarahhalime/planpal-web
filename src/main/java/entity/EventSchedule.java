package entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Represents the schedule of an event.
 */
public final class EventSchedule {

    private static final String MISSING_START_MESSAGE =
            "The event start date and time are required.";

    private static final String INCOMPLETE_END_MESSAGE =
            "The event end date and time must both be provided.";

    private static final String INVALID_END_MESSAGE =
            "The event end cannot be before the event start.";

    private final LocalDate startDate;
    private final LocalTime startTime;
    private final LocalDate endDate;
    private final LocalTime endTime;

    /**
     * Creates an event schedule.
     *
     * @param startDate the event start date
     * @param startTime the event start time
     * @param endDate the optional event end date
     * @param endTime the optional event end time
     * @throws IllegalArgumentException if the schedule information is invalid
     */
    public EventSchedule(
            LocalDate startDate,
            LocalTime startTime,
            LocalDate endDate,
            LocalTime endTime) {

        if (startDate == null || startTime == null) {
            throw new IllegalArgumentException(MISSING_START_MESSAGE);
        }

        final boolean hasEndDate = endDate != null;
        final boolean hasEndTime = endTime != null;

        if (hasEndDate != hasEndTime) {
            throw new IllegalArgumentException(INCOMPLETE_END_MESSAGE);
        }

        if (hasEndDate) {
            final LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);

            final LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

            if (endDateTime.isBefore(startDateTime)) {
                throw new IllegalArgumentException(INVALID_END_MESSAGE);
            }
        }

        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
    }

    /**
     * Returns the event start date.
     *
     * @return the event start date
     */
    public LocalDate getStartDate() {
        return this.startDate;
    }

    /**
     * Returns the event start time.
     *
     * @return the event start time
     */
    public LocalTime getStartTime() {
        return this.startTime;
    }

    /**
     * Returns the event end date.
     *
     * @return the end date, or null if the event has no end
     */
    public LocalDate getEndDate() {
        return this.endDate;
    }

    /**
     * Returns the event end time.
     *
     * @return the end time, or null if the event has no end
     */
    public LocalTime getEndTime() {
        return this.endTime;
    }
}

package use_case.activity_validation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import entity.Activity;
import entity.Event;
import entity.EventSchedule;

/**
 * Validates activity dates and times against an event schedule and its existing activities.
 */
public final class ActivityScheduleValidator {

    private static final String EMPTY_TEXT = "";
    private static final String INVALID_DATE_MESSAGE = "Activity date is invalid.";
    private static final String INVALID_TIME_MESSAGE = "Activity time is invalid.";
    private static final String BEFORE_EVENT_MESSAGE = "Activity cannot be scheduled before the event starts.";
    private static final String AFTER_EVENT_MESSAGE = "Activity cannot be scheduled after the event ends.";
    private static final String CONFLICT_MESSAGE = "Another activity is already scheduled at this date and time.";
    private static final int NO_EXCLUDED_ACTIVITY = -1;

    private ActivityScheduleValidator() {
    }

    /**
     * Validates a new activity against the event schedule and existing activities.
     *
     * @param event the event containing the activity
     * @param dateText the activity date
     * @param timeText the activity time
     * @return an error message, or {@code null} when the activity schedule is valid
     */
    public static String validateNewActivity(Event event, String dateText, String timeText) {
        return validate(event, dateText, timeText, NO_EXCLUDED_ACTIVITY);
    }

    /**
     * Validates an edited activity against the event schedule and other activities.
     *
     * @param event the event containing the activity
     * @param dateText the activity date
     * @param timeText the activity time
     * @param activityIndex the activity being edited
     * @return an error message, or {@code null} when the activity schedule is valid
     */
    public static String validateEditedActivity(
            Event event,
            String dateText,
            String timeText,
            int activityIndex) {

        return validate(event, dateText, timeText, activityIndex);
    }

    private static String validate(Event event, String dateText, String timeText, int excludedActivityIndex) {
        final String cleanedDate = clean(dateText);
        final String cleanedTime = clean(timeText);
        String errorMessage = null;

        if (!isValidDate(cleanedDate)) {
            errorMessage = INVALID_DATE_MESSAGE;
        }
        else if (!isValidTime(cleanedTime)) {
            errorMessage = INVALID_TIME_MESSAGE;
        }
        else {
            final LocalDate activityDate = parseDate(cleanedDate);
            final LocalTime activityTime = parseTime(cleanedTime);

            if (activityDate != null) {
                errorMessage = validateEventRange(event.getEventSchedule(), activityDate, activityTime);
            }

            if (errorMessage == null && activityDate != null && activityTime != null
                    && hasConflictingActivity(
                    event.getActivityList(),
                    activityDate,
                    activityTime,
                    excludedActivityIndex)) {
                errorMessage = CONFLICT_MESSAGE;
            }
        }

        return errorMessage;
    }

    private static String validateEventRange(EventSchedule schedule, LocalDate activityDate, LocalTime activityTime) {
        final LocalDate startDate = schedule.getStartDate();
        final LocalDate endDate = schedule.getEndDate();
        String errorMessage = null;

        if (activityDate.isBefore(startDate)) {
            errorMessage = BEFORE_EVENT_MESSAGE;
        }
        else if (endDate != null && activityDate.isAfter(endDate)) {
            errorMessage = AFTER_EVENT_MESSAGE;
        }
        else if (activityTime != null) {
            errorMessage = validateEventDateTimeRange(schedule, activityDate, activityTime);
        }

        return errorMessage;
    }

    private static String validateEventDateTimeRange(
            EventSchedule schedule,
            LocalDate activityDate,
            LocalTime activityTime) {

        final LocalDateTime activityDateTime = LocalDateTime.of(activityDate, activityTime);
        final LocalDateTime startDateTime = LocalDateTime.of(schedule.getStartDate(), schedule.getStartTime());
        final LocalDate endDate = schedule.getEndDate();
        final LocalTime endTime = schedule.getEndTime();
        String errorMessage = null;

        if (activityDateTime.isBefore(startDateTime)) {
            errorMessage = BEFORE_EVENT_MESSAGE;
        }
        else if (endDate != null && endTime != null) {
            final LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

            if (activityDateTime.isAfter(endDateTime)) {
                errorMessage = AFTER_EVENT_MESSAGE;
            }
        }

        return errorMessage;
    }

    private static boolean hasConflictingActivity(
            List<Activity> activities,
            LocalDate activityDate,
            LocalTime activityTime,
            int excludedActivityIndex) {

        boolean conflictFound = false;
        int activityIndex = 0;

        while (activityIndex < activities.size() && !conflictFound) {
            if (activityIndex != excludedActivityIndex) {
                final Activity activity = activities.get(activityIndex);
                final LocalDate existingDate = tryParseDate(activity.getDate());
                final LocalTime existingTime = tryParseTime(activity.getTime());

                conflictFound = activityDate.equals(existingDate) && activityTime.equals(existingTime);
            }
            activityIndex++;
        }

        return conflictFound;
    }

    private static boolean isValidDate(String dateText) {
        boolean valid = true;

        if (!dateText.isBlank()) {
            try {
                LocalDate.parse(dateText);
            }
            catch (DateTimeParseException exception) {
                valid = false;
            }
        }

        return valid;
    }

    private static boolean isValidTime(String timeText) {
        boolean valid = true;

        if (!timeText.isBlank()) {
            try {
                LocalTime.parse(timeText);
            }
            catch (DateTimeParseException exception) {
                valid = false;
            }
        }

        return valid;
    }

    private static LocalDate parseDate(String dateText) {
        LocalDate date = null;

        if (!dateText.isBlank()) {
            date = LocalDate.parse(dateText);
        }

        return date;
    }

    private static LocalTime parseTime(String timeText) {
        LocalTime time = null;

        if (!timeText.isBlank()) {
            time = LocalTime.parse(timeText);
        }

        return time;
    }

    private static LocalDate tryParseDate(String dateText) {
        LocalDate date = null;
        final String cleanedDate = clean(dateText);

        if (isValidDate(cleanedDate)) {
            date = parseDate(cleanedDate);
        }

        return date;
    }

    private static LocalTime tryParseTime(String timeText) {
        LocalTime time = null;
        final String cleanedTime = clean(timeText);

        if (isValidTime(cleanedTime)) {
            time = parseTime(cleanedTime);
        }

        return time;
    }

    private static String clean(String value) {
        final String cleanedValue;

        if (value == null) {
            cleanedValue = EMPTY_TEXT;
        }
        else {
            cleanedValue = value.trim();
        }

        return cleanedValue;
    }
}

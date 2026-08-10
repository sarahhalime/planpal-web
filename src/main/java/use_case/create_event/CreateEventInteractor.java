package use_case.create_event;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import entity.Event;
import entity.EventDetails;
import entity.EventFactory;
import entity.EventSchedule;

/**
 * The interactor for the create-event use case.
 */
public final class CreateEventInteractor implements CreateEventInputBoundary {

    private static final String MISSING_INFORMATION_MESSAGE =
            "Create event information is missing.";
    private static final String EMPTY_NAME_MESSAGE =
            "The event name cannot be empty.";
    private static final String MISSING_SCHEDULE_MESSAGE =
            "The event start date and time are required.";
    private static final String MISSING_CURRENCY_MESSAGE =
            "An event currency must be selected.";
    private static final String INVALID_SCHEDULE_MESSAGE =
            "The event schedule is invalid.";

    private final CreateEventDataAccessInterface dataAccessObject;
    private final CreateEventOutputBoundary presenter;
    private final EventFactory eventFactory;

    /**
     * Creates a create-event interactor.
     *
     * @param dataAccessObject the create-event data-access object
     * @param presenter        the create-event output boundary
     * @param eventFactory     the event factory
     */
    public CreateEventInteractor(
            CreateEventDataAccessInterface dataAccessObject,
            CreateEventOutputBoundary presenter,
            EventFactory eventFactory) {

        this.dataAccessObject = dataAccessObject;
        this.presenter = presenter;
        this.eventFactory = eventFactory;
    }

    /**
     * Creates an event using the supplied input data.
     *
     * @param inputData the create-event input data
     */
    @Override
    public void execute(CreateEventInputData inputData) {
        if (this.findMissingField(inputData) == null) {
            final EventSchedule schedule = this.parseSchedule(inputData);
            if (schedule != null) {
                this.saveNewEvent(inputData, schedule);
            }
        }
    }

    /**
     * Reports the first missing required field, if there is one.
     *
     * @param inputData the create-event input data
     * @return the reported message, or null when nothing is missing
     */
    private String findMissingField(CreateEventInputData inputData) {
        String message = null;
        if (inputData == null) {
            message = MISSING_INFORMATION_MESSAGE;
            this.presenter.prepareFailView(message);
        }
        else if (inputData.getEventName() == null || inputData.getEventName().isBlank()) {
            message = EMPTY_NAME_MESSAGE;
            this.presenter.prepareFailView(message, CreateEventErrorField.EVENT_NAME);
        }
        else if (inputData.getStartDate() == null || inputData.getStartDate().isBlank()) {
            message = MISSING_SCHEDULE_MESSAGE;
            this.presenter.prepareFailView(message, CreateEventErrorField.START_DATE);
        }
        else if (inputData.getStartTime() == null || inputData.getStartTime().isBlank()) {
            message = MISSING_SCHEDULE_MESSAGE;
            this.presenter.prepareFailView(message, CreateEventErrorField.START_TIME);
        }
        else if (inputData.getEventCurrency() == null || inputData.getEventCurrency().isBlank()) {
            message = MISSING_CURRENCY_MESSAGE;
            this.presenter.prepareFailView(message, CreateEventErrorField.EVENT_CURRENCY);
        }
        return message;
    }

    private EventSchedule parseSchedule(CreateEventInputData inputData) {
        EventSchedule schedule = null;
        final LocalDate startDate =
                this.parseDate(inputData.getStartDate(), CreateEventErrorField.START_DATE);

        if (startDate != null) {
            final LocalTime startTime =
                    this.parseTime(inputData.getStartTime(), CreateEventErrorField.START_TIME);
            if (startTime != null) {
                schedule = this.scheduleWithEnd(inputData, startDate, startTime);
            }
        }
        return schedule;
    }

    private EventSchedule scheduleWithEnd(CreateEventInputData inputData,
                                          LocalDate startDate, LocalTime startTime) {
        final boolean hasEndDate = inputData.getEndDate() != null
                && !inputData.getEndDate().isBlank();
        final boolean hasEndTime = inputData.getEndTime() != null
                && !inputData.getEndTime().isBlank();

        EventSchedule schedule = null;
        if (hasEndDate && !hasEndTime) {
            this.presenter.prepareFailView(INVALID_SCHEDULE_MESSAGE,
                    CreateEventErrorField.END_TIME);
        }
        else if (!hasEndDate && hasEndTime) {
            this.presenter.prepareFailView(INVALID_SCHEDULE_MESSAGE,
                    CreateEventErrorField.END_DATE);
        }
        else if (hasEndDate) {
            schedule = this.parseEndSchedule(inputData, startDate, startTime);
        }
        else {
            schedule = this.buildSchedule(startDate, startTime, null, null);
        }
        return schedule;
    }

    private EventSchedule parseEndSchedule(CreateEventInputData inputData,
                                           LocalDate startDate, LocalTime startTime) {
        EventSchedule schedule = null;
        final LocalDate endDate =
                this.parseDate(inputData.getEndDate(), CreateEventErrorField.END_DATE);

        if (endDate != null) {
            final LocalTime endTime =
                    this.parseTime(inputData.getEndTime(), CreateEventErrorField.END_TIME);
            if (endTime != null) {
                schedule = this.buildSchedule(startDate, startTime, endDate, endTime);
            }
        }
        return schedule;
    }

    private LocalDate parseDate(String text, CreateEventErrorField errorField) {
        LocalDate parsed = null;
        try {
            parsed = LocalDate.parse(text);
        }
        catch (DateTimeException exception) {
            this.presenter.prepareFailView(INVALID_SCHEDULE_MESSAGE, errorField);
        }
        return parsed;
    }

    private LocalTime parseTime(String text, CreateEventErrorField errorField) {
        LocalTime parsed = null;
        try {
            parsed = LocalTime.parse(text);
        }
        catch (DateTimeException exception) {
            this.presenter.prepareFailView(INVALID_SCHEDULE_MESSAGE, errorField);
        }
        return parsed;
    }

    private EventSchedule buildSchedule(LocalDate startDate, LocalTime startTime,
                                        LocalDate endDate, LocalTime endTime) {
        EventSchedule schedule = null;
        try {
            schedule = new EventSchedule(startDate, startTime, endDate, endTime);
        }
        catch (IllegalArgumentException exception) {
            this.presenter.prepareFailView(INVALID_SCHEDULE_MESSAGE,
                    CreateEventErrorField.END_SCHEDULE);
        }
        return schedule;
    }

    private void saveNewEvent(CreateEventInputData inputData, EventSchedule eventSchedule) {
        final int eventId = this.dataAccessObject.getNextEventId();
        final String eventName = inputData.getEventName().trim();
        final List<String> attendees = new ArrayList<>();
        attendees.add(inputData.getUsername());
        final EventDetails details = new EventDetails(
                                             eventName,
                                             inputData.getEventDescription(),
                                             inputData.getEventLocation(),
                                             inputData.getEventBudget(),
                                             inputData.getEventCurrency().trim().toUpperCase());
        final Event event = this.eventFactory.createEvent(
                eventId, details, eventSchedule, attendees, List.of(), List.of());
        this.dataAccessObject.saveEvent(event);
        final CreateEventOutputData outputData =
                new CreateEventOutputData(
                        eventId,
                        eventName,
                        inputData.getUsername(),
                        inputData.getStartDate()
                );
        this.presenter.prepareSuccessView(outputData);
    }

}

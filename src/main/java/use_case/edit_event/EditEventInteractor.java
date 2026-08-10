package use_case.edit_event;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;

import entity.Event;
import entity.EventDetails;
import entity.EventFactory;
import entity.EventSchedule;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/**
 * The interactor for the edit-event use case.
 */
public final class EditEventInteractor implements EditEventInputBoundary {

    private static final String MISSING_INFORMATION_MESSAGE =
            "Edit event information is missing.";
    private static final String EMPTY_NAME_MESSAGE =
            "The event name cannot be empty.";
    private static final String MISSING_SCHEDULE_MESSAGE =
            "The event start date and time are required.";
    private static final String INVALID_SCHEDULE_MESSAGE =
            "The event schedule is invalid.";

    private final EditEventDataAccessInterface dataAccessObject;
    private final EditEventOutputBoundary presenter;
    private final EventFactory eventFactory;

    /**
     * Creates an edit-event interactor.
     *
     * @param dataAccessObject the edit-event data-access object
     * @param presenter the edit-event output boundary
     * @param eventFactory the event factory
     */
    public EditEventInteractor(
            EditEventDataAccessInterface dataAccessObject,
            EditEventOutputBoundary presenter,
            EventFactory eventFactory) {

        this.dataAccessObject = dataAccessObject;
        this.presenter = presenter;
        this.eventFactory = eventFactory;
    }

    /**
     * Updates an event using the supplied input data.
     *
     * @param inputData the edit-event input data
     */
    @Override
    public void execute(EditEventInputData inputData) {
        if (inputData == null) {
            this.presenter.prepareFailView(MISSING_INFORMATION_MESSAGE);
        }
        else if (inputData.getEventName() == null
                || inputData.getEventName().isBlank()) {
            this.presenter.prepareFailView(EMPTY_NAME_MESSAGE);
        }
        else if (inputData.getStartDate() == null
                || inputData.getStartDate().isBlank()
                || inputData.getStartTime() == null
                || inputData.getStartTime().isBlank()) {
            this.presenter.prepareFailView(MISSING_SCHEDULE_MESSAGE);
        }
        else {
            this.updateEvent(inputData);
        }
    }

    private void updateEvent(EditEventInputData inputData) {
        try {
            final Event event = this.dataAccessObject.getEvent(
                    inputData.getEventId()
            );
            final LocalDate startDate = LocalDate.parse(inputData.getStartDate());
            final LocalTime startTime = LocalTime.parse(inputData.getStartTime());

            LocalDate endDate = null;
            LocalTime endTime = null;

            if (inputData.getEndDate() != null
                    && !inputData.getEndDate().isBlank()) {
                endDate = LocalDate.parse(inputData.getEndDate());
            }

            if (inputData.getEndTime() != null
                    && !inputData.getEndTime().isBlank()) {
                endTime = LocalTime.parse(inputData.getEndTime());
            }

            final EventSchedule eventSchedule =
                    new EventSchedule(startDate, startTime, endDate, endTime);

            final Event updatedEvent = this.eventFactory.createEvent(
                                                                event.getEventId(),
                                                                new EventDetails(
                                                                        inputData.getEventName().trim(),
                                                                        inputData.getEventDescription(),
                                                                        inputData.getEventLocation(),
                                                                        inputData.getEventBudget(),
                                                                        "CAD"),
                                                                eventSchedule,
                                                                event.getAttendeeUsernames(),
                                                                event.getExpenseList(),
                                                                event.getActivityList()
                                                            );

            this.dataAccessObject.saveEvent(updatedEvent);

            final EditEventOutputData outputData =
                    new EditEventOutputData(
                            updatedEvent,
                            updatedEvent.getEventId(),
                            updatedEvent.getEventName()
                    );

            this.presenter.prepareSuccessView(outputData);
        }
        catch (WhoOwesWhatDataAccessException exception) {
            this.presenter.prepareFailView(exception.getMessage());
        }
        catch (DateTimeException | IllegalArgumentException exception) {
            this.presenter.prepareFailView(INVALID_SCHEDULE_MESSAGE);
        }
    }
}

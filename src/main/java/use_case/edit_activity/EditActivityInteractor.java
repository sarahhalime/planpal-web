package use_case.edit_activity;

import java.util.ArrayList;
import java.util.List;

import entity.Activity;
import entity.ActivityFactory;
import entity.Event;
import entity.EventDetails;
import entity.EventFactory;
import use_case.activity_validation.ActivityScheduleValidator;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/**
 * Edits an activity in a saved event.
 */
public final class EditActivityInteractor implements EditActivityInputBoundary {

    private static final String MISSING_INPUT_MESSAGE = "Activity information is required.";
    private static final String NAME_REQUIRED_MESSAGE = "Activity name is required.";
    private static final String ACTIVITY_NOT_FOUND_MESSAGE = "The selected activity no longer exists.";
    private static final String EMPTY_TEXT = "";

    private final EditActivityDataAccessInterface dataAccessObject;
    private final EditActivityOutputBoundary presenter;
    private final ActivityFactory activityFactory;
    private final EventFactory eventFactory;

    /**
     * Creates an edit-activity interactor.
     *
     * @param dataAccessObject the event data-access object
     * @param presenter the edit-activity presenter
     * @param activityFactory the activity factory
     * @param eventFactory the event factory
     */
    public EditActivityInteractor(
            EditActivityDataAccessInterface dataAccessObject,
            EditActivityOutputBoundary presenter,
            ActivityFactory activityFactory,
            EventFactory eventFactory) {

        this.dataAccessObject = dataAccessObject;
        this.presenter = presenter;
        this.activityFactory = activityFactory;
        this.eventFactory = eventFactory;
    }

    /**
     * Edits the selected activity.
     *
     * @param inputData the edit-activity input data
     */
    @Override
    public void execute(EditActivityInputData inputData) {
        if (inputData == null) {
            this.presenter.prepareFailView(MISSING_INPUT_MESSAGE);
        }
        else {
            this.editActivity(inputData);
        }
    }

    private void editActivity(EditActivityInputData inputData) {
        final String activityName = this.clean(inputData.getActivityName());

        if (activityName.isBlank()) {
            this.presenter.prepareFailView(NAME_REQUIRED_MESSAGE);
        }
        else {
            this.saveEditedActivity(inputData, activityName);
        }
    }

    private void saveEditedActivity(EditActivityInputData inputData, String activityName) {
        try {
            final Event event = this.dataAccessObject.getEvent(inputData.getEventId());
            final List<Activity> activities = new ArrayList<>(event.getActivityList());
            final int activityIndex = inputData.getActivityIndex();

            if (activityIndex < 0 || activityIndex >= activities.size()) {
                this.presenter.prepareFailView(ACTIVITY_NOT_FOUND_MESSAGE);
            }
            else {
                this.validateAndSaveActivity(inputData, activityName, event, activities, activityIndex);
            }
        }
        catch (WhoOwesWhatDataAccessException exception) {
            this.presenter.prepareFailView(exception.getMessage());
        }
    }

    private void validateAndSaveActivity(
            EditActivityInputData inputData,
            String activityName,
            Event event,
            List<Activity> activities,
            int activityIndex) {

        final String date = this.clean(inputData.getDate());
        final String time = this.clean(inputData.getTime());
        final String validationError = ActivityScheduleValidator.validateEditedActivity(
                event,
                date,
                time,
                activityIndex
        );

        if (validationError != null) {
            this.presenter.prepareFailView(validationError);
        }
        else {
            final Activity activity = this.activityFactory.create(
                    activityName,
                    date,
                    time,
                    this.clean(inputData.getLocation())
            );

            activities.set(activityIndex, activity);
            this.dataAccessObject.saveEvent(this.copyEvent(event, activities));
            this.presenter.prepareSuccessView(
                    new EditActivityOutputData(
                            activityIndex,
                            activity.getActivityName(),
                            activity.getDate(),
                            activity.getTime(),
                            activity.getLocation()
                    )
            );
        }
    }

    private Event copyEvent(Event event, List<Activity> activities) {
        return this.eventFactory.createEvent(
                                        event.getEventId(),
                                        new EventDetails(
                                                event.getEventName(),
                                                event.getEventDescription(),
                                                event.getEventLocation(),
                                                event.getEventBudget(),
                                                event.getEventCurrency()),
                                        event.getEventSchedule(),
                                        event.getAttendeeUsernames(),
                                        event.getExpenseList(),
                                        activities
                                    );
    }

    private String clean(String value) {
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

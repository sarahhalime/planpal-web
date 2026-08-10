package use_case.add_activity;

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
 * Adds an activity to an existing event.
 */
public final class AddActivityInteractor implements AddActivityInputBoundary {

    private static final String EMPTY_TEXT = "";
    private static final String MISSING_INPUT_MESSAGE =
            "Add activity information is missing.";
    private static final String NAME_REQUIRED_MESSAGE =
            "An activity name is required.";

    private final AddActivityDataAccessInterface dataAccessObject;
    private final AddActivityOutputBoundary presenter;
    private final ActivityFactory activityFactory;
    private final EventFactory eventFactory;

    /**
     * Creates an add-activity interactor.
     *
     * @param dataAccessObject the event data-access object
     * @param presenter the add-activity output boundary
     * @param activityFactory the activity factory
     * @param eventFactory the event factory
     */
    public AddActivityInteractor(
            AddActivityDataAccessInterface dataAccessObject,
            AddActivityOutputBoundary presenter,
            ActivityFactory activityFactory,
            EventFactory eventFactory) {

        this.dataAccessObject = dataAccessObject;
        this.presenter = presenter;
        this.activityFactory = activityFactory;
        this.eventFactory = eventFactory;
    }

    /**
     * Adds an activity to the event identified by the input data.
     *
     * @param inputData the add-activity input data
     */
    @Override
    public void execute(AddActivityInputData inputData) {
        if (inputData == null) {
            this.presenter.prepareFailView(MISSING_INPUT_MESSAGE);
        }
        else {
            this.addActivity(inputData);
        }
    }

    private void addActivity(AddActivityInputData inputData) {
        final String activityName =
                this.clean(inputData.getActivityName());

        if (activityName.isBlank()) {
            this.presenter.prepareFailView(NAME_REQUIRED_MESSAGE);
        }
        else {
            this.saveActivity(inputData, activityName);
        }
    }

    private void saveActivity(
            AddActivityInputData inputData,
            String activityName) {

        try {
            final Event event = this.dataAccessObject.getEvent(
                    inputData.getEventId()
            );

            final String date = this.clean(inputData.getDate());
            final String time = this.clean(inputData.getTime());
            final String validationError = ActivityScheduleValidator.validateNewActivity(
                    event,
                    date,
                    time
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
                final List<Activity> updatedActivities = new ArrayList<>(event.getActivityList());

                updatedActivities.add(activity);
                this.dataAccessObject.saveEvent(this.copyEvent(event, updatedActivities));
                this.presenter.prepareSuccessView(
                        new AddActivityOutputData(
                                activity.getActivityName(),
                                activity.getDate(),
                                activity.getTime(),
                                activity.getLocation()
                        )
                );
            }
        }
        catch (WhoOwesWhatDataAccessException exception) {
            this.presenter.prepareFailView(exception.getMessage());
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

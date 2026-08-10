package use_case.remove_activity;

import java.util.ArrayList;
import java.util.List;

import entity.Activity;
import entity.Event;
import entity.EventDetails;
import entity.EventFactory;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/**
 * Removes an activity from a saved event.
 */
public final class RemoveActivityInteractor implements RemoveActivityInputBoundary {

    private static final String MISSING_INPUT_MESSAGE = "Activity information is required.";
    private static final String ACTIVITY_NOT_FOUND_MESSAGE = "The selected activity no longer exists.";

    private final RemoveActivityDataAccessInterface dataAccessObject;
    private final RemoveActivityOutputBoundary presenter;
    private final EventFactory eventFactory;

    /**
     * Creates a remove-activity interactor.
     *
     * @param dataAccessObject the event data-access object
     * @param presenter the remove-activity presenter
     * @param eventFactory the event factory
     */
    public RemoveActivityInteractor(
            RemoveActivityDataAccessInterface dataAccessObject,
            RemoveActivityOutputBoundary presenter,
            EventFactory eventFactory) {

        this.dataAccessObject = dataAccessObject;
        this.presenter = presenter;
        this.eventFactory = eventFactory;
    }

    /**
     * Removes the selected activity.
     *
     * @param inputData the remove-activity input data
     */
    @Override
    public void execute(RemoveActivityInputData inputData) {
        if (inputData == null) {
            this.presenter.prepareFailView(MISSING_INPUT_MESSAGE);
        }
        else {
            this.removeActivity(inputData);
        }
    }

    private void removeActivity(RemoveActivityInputData inputData) {
        try {
            final Event event = this.dataAccessObject.getEvent(inputData.getEventId());
            final List<Activity> activities = new ArrayList<>(event.getActivityList());
            final int activityIndex = inputData.getActivityIndex();

            if (activityIndex < 0 || activityIndex >= activities.size()) {
                this.presenter.prepareFailView(ACTIVITY_NOT_FOUND_MESSAGE);
            }
            else {
                final Activity removedActivity = activities.remove(activityIndex);
                this.dataAccessObject.saveEvent(this.copyEvent(event, activities));
                this.presenter.prepareSuccessView(
                        new RemoveActivityOutputData(
                                activityIndex,
                                removedActivity.getActivityName()
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
}

package use_case.remove_guest;

import java.util.ArrayList;
import java.util.List;

import entity.Event;
import entity.EventDetails;
import entity.EventFactory;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/**
 * Removes an attendee from an event.
 */
public final class RemoveGuestInteractor implements RemoveGuestInputBoundary {

    private static final String ATTENDEE_NOT_FOUND_MESSAGE =
            "No attendee was found with that username.";

    private final RemoveGuestDataAccessInterface dataAccessObject;
    private final RemoveGuestOutputBoundary presenter;
    private final EventFactory eventFactory;

    /**
     * Creates a remove-guest interactor.
     *
     * @param dataAccessObject the event data-access object
     * @param presenter the remove-guest output boundary
     * @param eventFactory the event factory
     */
    public RemoveGuestInteractor(
            RemoveGuestDataAccessInterface dataAccessObject,
            RemoveGuestOutputBoundary presenter,
            EventFactory eventFactory) {

        this.dataAccessObject = dataAccessObject;
        this.presenter = presenter;
        this.eventFactory = eventFactory;
    }

    /**
     * Removes the requested attendee from an event.
     *
     * @param inputData the remove-guest input data
     */
    @Override
    public void execute(RemoveGuestInputData inputData) {
        try {
            final Event event = this.dataAccessObject.getEvent(
                    inputData.getEventId()
            );

            final List<String> updatedAttendees = new ArrayList<>();
            String removedUsername = null;

            for (final String username : event.getAttendeeUsernames()) {
                if (username.equalsIgnoreCase(inputData.getUsername())) {
                    removedUsername = username;
                }
                else {
                    updatedAttendees.add(username);
                }
            }

            if (removedUsername == null) {
                this.presenter.prepareFailView(
                        ATTENDEE_NOT_FOUND_MESSAGE
                );
            }
            else {
                this.saveUpdatedEvent(
                        event,
                        updatedAttendees,
                        removedUsername
                );
            }
        }
        catch (WhoOwesWhatDataAccessException exception) {
            this.presenter.prepareFailView(exception.getMessage());
        }
    }

    private void saveUpdatedEvent(
            Event event,
            List<String> updatedAttendees,
            String removedUsername) {

        final Event updatedEvent = this.eventFactory.createEvent(
                                                            event.getEventId(),
                                                            new EventDetails(
                                                                    event.getEventName(),
                                                                    event.getEventDescription(),
                                                                    event.getEventLocation(),
                                                                    event.getEventBudget(),
                                                                    event.getEventCurrency()),
                                                            event.getEventSchedule(),
                                                            updatedAttendees,
                                                            event.getExpenseList(),
                                                            event.getActivityList()
                                                        );

        this.dataAccessObject.saveEvent(updatedEvent);

        final RemoveGuestOutputData outputData =
                new RemoveGuestOutputData(
                        updatedEvent.getEventId(),
                        removedUsername,
                        false
                );

        this.presenter.prepareSuccessView(outputData);
    }
}

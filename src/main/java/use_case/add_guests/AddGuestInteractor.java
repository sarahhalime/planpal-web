package use_case.add_guests;

import java.util.ArrayList;
import java.util.List;

import entity.Event;
import entity.EventDetails;
import entity.EventFactory;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/**
 * Adds a registered user to an existing event.
 */
public final class AddGuestInteractor implements AddGuestInputBoundary {

    private static final String EMPTY_TEXT = "";
    private static final String USERNAME_REQUIRED_MESSAGE =
            "A username is required.";
    private static final String UNKNOWN_USER_MESSAGE =
            "No registered user has that username.";
    private static final String ALREADY_ATTENDING_MESSAGE =
            "That user is already on the event.";

    private final AddGuestDataAccessInterface eventDataAccessObject;
    private final AddGuestUserDataAccessInterface userDataAccessObject;
    private final AddGuestOutputBoundary presenter;
    private final EventFactory eventFactory;

    /**
     * Creates an add-guest interactor.
     *
     * @param eventDataAccessObject the event data-access object
     * @param userDataAccessObject the user data-access object
     * @param presenter the add-guest output boundary
     * @param eventFactory the event factory
     */
    public AddGuestInteractor(
            AddGuestDataAccessInterface eventDataAccessObject,
            AddGuestUserDataAccessInterface userDataAccessObject,
            AddGuestOutputBoundary presenter,
            EventFactory eventFactory) {

        this.eventDataAccessObject = eventDataAccessObject;
        this.userDataAccessObject = userDataAccessObject;
        this.presenter = presenter;
        this.eventFactory = eventFactory;
    }

    /**
     * Adds a guest using the supplied input data.
     *
     * @param inputData the add-guest input data
     */
    @Override
    public void execute(AddGuestInputData inputData) {
        final String username = this.cleanUsername(inputData.getUsername());

        if (username.isBlank()) {
            this.presenter.prepareFailView(USERNAME_REQUIRED_MESSAGE);
        }
        else if (!this.userDataAccessObject.existsByUsername(username)) {
            this.presenter.prepareFailView(UNKNOWN_USER_MESSAGE);
        }
        else {
            this.addGuest(inputData.getEventId(), username);
        }
    }

    @Override
    public void setAvaliableGuests(int eventId) {
        try {
            final Event event = this.eventDataAccessObject.getEvent(eventId);
            final List<String> availableUsernames =
                    new ArrayList<>(this.userDataAccessObject.getAllUsernames());

            availableUsernames.removeIf(
                    username -> this.isAlreadyAttending(event, username)
            );

            this.presenter.setAvaliableUsernames(availableUsernames);
        }
        catch (WhoOwesWhatDataAccessException exception) {
            this.presenter.prepareFailView(exception.getMessage());
        }
    }

    private void addGuest(int eventId, String username) {
        try {
            final Event event = this.eventDataAccessObject.getEvent(eventId);

            if (this.isAlreadyAttending(event, username)) {
                this.presenter.prepareFailView(ALREADY_ATTENDING_MESSAGE);
            }
            else {
                this.saveGuest(event, username);
            }
        }
        catch (WhoOwesWhatDataAccessException exception) {
            this.presenter.prepareFailView(exception.getMessage());
        }
    }

    private void saveGuest(Event event, String username) {
        final List<String> updatedAttendees =
                new ArrayList<>(event.getAttendeeUsernames());

        updatedAttendees.add(username);

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

        this.eventDataAccessObject.saveEvent(updatedEvent);

        final AddGuestOutputData outputData = new AddGuestOutputData(
                updatedEvent.getEventId(),
                username,
                false
        );

        this.presenter.prepareSuccessView(outputData);
    }

    private String cleanUsername(String username) {
        final String cleanedUsername;

        if (username == null) {
            cleanedUsername = EMPTY_TEXT;
        }
        else {
            cleanedUsername = username.trim();
        }

        return cleanedUsername;
    }

    private boolean isAlreadyAttending(Event event, String username) {
        boolean found = false;

        for (final String existingUsername : event.getAttendeeUsernames()) {
            if (existingUsername.equalsIgnoreCase(username)) {
                found = true;
            }
        }

        return found;
    }
}

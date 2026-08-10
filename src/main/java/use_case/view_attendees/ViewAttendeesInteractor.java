package use_case.view_attendees;

import java.util.ArrayList;
import java.util.List;

import entity.Event;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/**
 * Loads the registered users attending an event and their profile pictures.
 */
public final class ViewAttendeesInteractor implements ViewAttendeesInputBoundary {
    private final ViewAttendeesDataAccessInterface eventDataAccessObject;
    private final ViewAttendeesUserDataAccessInterface userDataAccessObject;
    private final ViewAttendeesProfileDataAccessInterface profileDataAccessObject;
    private final ViewAttendeesOutputBoundary presenter;

    /**
     * Creates a view-attendees interactor.
     *
     * @param eventDataAccessObject event data access
     * @param userDataAccessObject user data access
     * @param profileDataAccessObject profile data access
     * @param presenter output boundary
     */
    public ViewAttendeesInteractor(
            ViewAttendeesDataAccessInterface eventDataAccessObject,
            ViewAttendeesUserDataAccessInterface userDataAccessObject,
            ViewAttendeesProfileDataAccessInterface profileDataAccessObject,
            ViewAttendeesOutputBoundary presenter) {
        this.eventDataAccessObject = eventDataAccessObject;
        this.userDataAccessObject = userDataAccessObject;
        this.profileDataAccessObject = profileDataAccessObject;
        this.presenter = presenter;
    }

    @Override
    public void execute(ViewAttendeesInputData inputData) {
        try {
            final Event event = this.eventDataAccessObject.getEvent(inputData.getEventId());
            final List<ViewAttendeeData> attendees = new ArrayList<>();

            for (final String username : event.getAttendeeUsernames()) {
                attendees.add(this.createAttendeeData(username));
            }
            this.presenter.prepareSuccessView(new ViewAttendeesOutputData(attendees));
        }
        catch (WhoOwesWhatDataAccessException exception) {
            this.presenter.prepareFailView(exception.getMessage());
        }
    }

    private ViewAttendeeData createAttendeeData(String username) {
        String displayName = this.userDataAccessObject.getDisplayName(username);
        if (displayName == null || displayName.isBlank()) {
            displayName = username;
        }
        return new ViewAttendeeData(
                username,
                displayName,
                this.profileDataAccessObject.getProfilePicture(username)
        );
    }
}

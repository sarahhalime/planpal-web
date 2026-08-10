package use_case.view_attendees;

/**
 * User data required by the view-attendees use case.
 */
public interface ViewAttendeesUserDataAccessInterface {

    /**
     * Returns the display name for a username.
     *
     * @param username the username
     * @return the display name, or null when unavailable
     */
    String getDisplayName(String username);
}

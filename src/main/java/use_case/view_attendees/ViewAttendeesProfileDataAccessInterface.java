package use_case.view_attendees;

/**
 * Profile data required by the view-attendees use case.
 */
public interface ViewAttendeesProfileDataAccessInterface {

    /**
     * Returns the profile picture for a username.
     *
     * @param username the username
     * @return picture bytes, or null when no picture is set
     */
    byte[] getProfilePicture(String username);
}

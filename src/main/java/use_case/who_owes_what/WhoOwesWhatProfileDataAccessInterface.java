package use_case.who_owes_what;

/**
 * Provides profile pictures for attendees shown in balance views.
 */
public interface WhoOwesWhatProfileDataAccessInterface {

    /**
     * Returns the profile picture stored for a username.
     *
     * @param username attendee username
     * @return profile-picture bytes, or null when no picture is stored
     */
    byte[] getProfilePicture(String username);
}

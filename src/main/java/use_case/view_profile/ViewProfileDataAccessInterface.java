package use_case.view_profile;

/**
 * The data access interface for reading a user's profile details.
 */
public interface ViewProfileDataAccessInterface {

    /**
     * Returns the user's bio, or an empty string if none is set.
     * @param username the user whose bio is requested
     * @return the bio text
     */
    String getBio(String username);

    /**
     * Returns the user's profile picture bytes, or null if none is set.
     * @param username the user whose picture is requested
     * @return the image bytes, or null
     */
    byte[] getProfilePicture(String username);
}

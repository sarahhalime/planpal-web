package use_case.update_profile;

/**
 * The data access interface for updating a user's profile details.
 */
public interface UpdateProfileDataAccessInterface {

    /**
     * Sets the user's bio.
     * @param username the user whose bio is set
     * @param bio the new bio text
     */
    void setBio(String username, String bio);

    /**
     * Sets the user's profile picture.
     * @param username the user whose picture is set
     * @param profilePicture the image bytes
     */
    void setProfilePicture(String username, byte[] profilePicture);
}

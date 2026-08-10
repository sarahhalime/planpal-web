package use_case.update_profile;

/**
 * The input data for the update profile use case.
 */
public class UpdateProfileInputData {

    private final String username;
    private final String bio;
    private final byte[] profilePicture;
    private final boolean clearPicture;

    public UpdateProfileInputData(String username, String bio, byte[] profilePicture) {
        this(username, bio, profilePicture, false);
    }

    public UpdateProfileInputData(String username, String bio, byte[] profilePicture, boolean clearPicture) {
        this.username = username;
        this.bio = bio;
        this.profilePicture = profilePicture;
        this.clearPicture = clearPicture;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public boolean isClearPicture() {
        return clearPicture;
    }
}

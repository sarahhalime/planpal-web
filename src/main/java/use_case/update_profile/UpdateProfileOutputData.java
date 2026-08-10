package use_case.update_profile;

/**
 * The output data for the update profile use case.
 */
public class UpdateProfileOutputData {

    private final String username;
    private final String bio;

    public UpdateProfileOutputData(String username, String bio) {
        this.username = username;
        this.bio = bio;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }
}

package use_case.view_attendees;

/**
 * Read-only attendee information shown by the attendees feature.
 */
public final class ViewAttendeeData {
    private final String username;
    private final String displayName;
    private final byte[] profilePicture;

    /**
     * Creates attendee output data.
     *
     * @param username the attendee username
     * @param displayName the attendee display name
     * @param profilePicture the attendee profile-picture bytes
     */
    public ViewAttendeeData(String username, String displayName, byte[] profilePicture) {
        this.username = username;
        this.displayName = displayName;
        this.profilePicture = profilePicture;
    }

    /**
     * Returns the username.
     *
     * @return the username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Returns the display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Returns the profile-picture bytes.
     *
     * @return the profile-picture bytes, or null when no picture is set
     */
    public byte[] getProfilePicture() {
        return this.profilePicture;
    }
}

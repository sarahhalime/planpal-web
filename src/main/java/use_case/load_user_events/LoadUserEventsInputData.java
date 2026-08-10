package use_case.load_user_events;

/**
 * The input data for the load user events use case.
 */
public class LoadUserEventsInputData {

    private final String username;

    public LoadUserEventsInputData(String username) {
        this.username = username;
    }

    /**
     * Returns the username whose events should be loaded.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }
}

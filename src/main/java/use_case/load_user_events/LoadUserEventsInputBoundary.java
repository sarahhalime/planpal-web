package use_case.load_user_events;

/**
 * The input boundary for the load user events use case.
 */
public interface LoadUserEventsInputBoundary {

    /**
     * Executes the load user events use case.
     *
     * @param loadUserEventsInputData the information needed to load the user's events
     */
    void execute(LoadUserEventsInputData loadUserEventsInputData);
}

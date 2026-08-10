package use_case.load_user_events;

/**
 * Indicates that a user's saved events could not be loaded.
 */
public class LoadUserEventsDataException extends Exception {

    public LoadUserEventsDataException(String message) {
        super(message);
    }
}

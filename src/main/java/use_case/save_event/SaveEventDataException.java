package use_case.save_event;

/**
 * Indicates that an event could not be saved.
 */
public class SaveEventDataException extends Exception {

    public SaveEventDataException(String message) {
        super(message);
    }
}

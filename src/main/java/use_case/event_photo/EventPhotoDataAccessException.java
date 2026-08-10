package use_case.event_photo;

/**
 * Thrown when an event photo cannot be read or saved.
 */
public final class EventPhotoDataAccessException extends Exception {

    /**
     * Creates an event-photo data-access exception.
     *
     * @param message explanation of the failure
     */
    public EventPhotoDataAccessException(String message) {
        super(message);
    }
}

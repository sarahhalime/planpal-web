package use_case.event_photo;

/**
 * Input boundary for changing an event photo.
 */
public interface EventPhotoInputBoundary {

    /**
     * Saves the supplied photo for the selected event.
     *
     * @param inputData event-photo input
     */
    void execute(EventPhotoInputData inputData);
}

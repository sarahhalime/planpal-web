package use_case.event_photo;

/**
 * Output boundary for changing an event photo.
 */
public interface EventPhotoOutputBoundary {

    /**
     * Presents a successfully saved event photo.
     *
     * @param outputData saved event-photo data
     */
    void prepareSuccessView(EventPhotoOutputData outputData);

    /**
     * Presents an event-photo failure.
     *
     * @param errorMessage failure message
     */
    void prepareFailView(String errorMessage);
}

package use_case.event_photo;

/**
 * Saves a photo for an event.
 */
public final class EventPhotoInteractor implements EventPhotoInputBoundary {

    private static final String EMPTY_PHOTO_MESSAGE =
            "Choose an image before saving the event photo.";

    private final EventPhotoDataAccessInterface dataAccessObject;
    private final EventPhotoOutputBoundary presenter;

    /**
     * Creates the event-photo interactor.
     *
     * @param dataAccessObject event-photo data access
     * @param presenter event-photo presenter
     */
    public EventPhotoInteractor(
            EventPhotoDataAccessInterface dataAccessObject,
            EventPhotoOutputBoundary presenter) {

        this.dataAccessObject = dataAccessObject;
        this.presenter = presenter;
    }

    @Override
    public void execute(EventPhotoInputData inputData) {
        final byte[] photoBytes = inputData.getPhotoBytes();

        if (photoBytes.length == 0) {
            this.presenter.prepareFailView(EMPTY_PHOTO_MESSAGE);
        }
        else {
            this.savePhoto(inputData.getEventId(), photoBytes);
        }
    }

    private void savePhoto(int eventId, byte[] photoBytes) {
        try {
            this.dataAccessObject.saveEventPhoto(eventId, photoBytes);
            this.presenter.prepareSuccessView(
                    new EventPhotoOutputData(photoBytes)
            );
        }
        catch (EventPhotoDataAccessException exception) {
            this.presenter.prepareFailView(exception.getMessage());
        }
    }
}

package use_case.event_photo;

/**
 * Stores event photos.
 */
public interface EventPhotoDataAccessInterface {

    /**
     * Returns the saved photo for an event.
     *
     * @param eventId event identifier
     * @return photo bytes, or null when no photo is saved
     */
    byte[] getEventPhoto(int eventId);

    /**
     * Saves a photo for an event.
     *
     * @param eventId event identifier
     * @param photoBytes image bytes
     * @throws EventPhotoDataAccessException when the event does not exist
     */
    void saveEventPhoto(int eventId, byte[] photoBytes) throws EventPhotoDataAccessException;
}

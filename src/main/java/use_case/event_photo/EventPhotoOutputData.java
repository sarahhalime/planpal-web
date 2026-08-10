package use_case.event_photo;

/**
 * Output data for a saved event photo.
 */
public final class EventPhotoOutputData {

    private final byte[] photoBytes;

    /**
     * Creates event-photo output data.
     *
     * @param photoBytes saved image bytes
     */
    public EventPhotoOutputData(byte[] photoBytes) {
        this.photoBytes = photoBytes.clone();
    }

    /**
     * Returns a copy of the saved image bytes.
     *
     * @return saved image bytes
     */
    public byte[] getPhotoBytes() {
        return this.photoBytes.clone();
    }
}

package use_case.event_photo;

/**
 * Input data for changing an event photo.
 */
public final class EventPhotoInputData {

    private final int eventId;
    private final byte[] photoBytes;

    /**
     * Creates event-photo input data.
     *
     * @param eventId event identifier
     * @param photoBytes image bytes
     */
    public EventPhotoInputData(int eventId, byte[] photoBytes) {
        this.eventId = eventId;
        this.photoBytes = photoBytes.clone();
    }

    /**
     * Returns the event identifier.
     *
     * @return event identifier
     */
    public int getEventId() {
        return this.eventId;
    }

    /**
     * Returns a copy of the photo bytes.
     *
     * @return photo bytes
     */
    public byte[] getPhotoBytes() {
        return this.photoBytes.clone();
    }
}

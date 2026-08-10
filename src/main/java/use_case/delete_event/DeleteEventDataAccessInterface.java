package use_case.delete_event;

/**
 * The data access interface for the delete event use case.
 */
public interface DeleteEventDataAccessInterface {

    /**
     * Deletes the event with the given id from storage.
     *
     * @param eventId the id of the event to delete
     */
    void deleteEvent(int eventId);
}

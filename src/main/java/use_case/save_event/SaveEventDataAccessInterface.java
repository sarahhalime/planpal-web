package use_case.save_event;

/**
 * The data-access interface for the save-event use case.
 */
public interface SaveEventDataAccessInterface {

    /**
     * Saves the specified event to a file.
     *
     * @param username the username of the event owner
     * @param eventId the ID of the event to save
     * @param filePath the destination file path
     * @return the name of the saved event
     * @throws SaveEventDataException if saving fails
     */
    String saveEvent(String username, int eventId, String filePath) throws SaveEventDataException;
}

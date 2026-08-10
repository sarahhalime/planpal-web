package use_case.save_event;

/**
 * The interactor for the save-event use case.
 */
public final class SaveEventInteractor
        implements SaveEventInputBoundary {

    private static final String MISSING_INPUT_MESSAGE = "Save event information is missing.";
    private static final String MISSING_USER_MESSAGE = "The current user is missing.";
    private static final String MISSING_EVENT_MESSAGE = "Select an event before saving.";
    private static final String MISSING_FILE_MESSAGE = "Choose a file before saving.";

    private final SaveEventDataAccessInterface saveEventDataAccessInterface;
    private final SaveEventOutputBoundary saveEventOutputBoundary;

    /**
     * Constructs the save-event interactor.
     *
     * @param saveEventDataAccessInterface data access
     * @param saveEventOutputBoundary presenter
     */
    public SaveEventInteractor(
            SaveEventDataAccessInterface saveEventDataAccessInterface,
            SaveEventOutputBoundary saveEventOutputBoundary) {
        this.saveEventDataAccessInterface = saveEventDataAccessInterface;
        this.saveEventOutputBoundary = saveEventOutputBoundary;
    }

    @Override
    public void execute(SaveEventInputData saveEventInputData) {
        if (saveEventInputData == null) {
            this.saveEventOutputBoundary.prepareFailView(
                    MISSING_INPUT_MESSAGE
            );
        }
        else if (saveEventInputData.getUsername() == null
                || saveEventInputData
                .getUsername().isBlank()) {
            this.saveEventOutputBoundary.prepareFailView(
                    MISSING_USER_MESSAGE
            );
        }
        else if (saveEventInputData.getEventId() <= 0) {
            this.saveEventOutputBoundary.prepareFailView(
                    MISSING_EVENT_MESSAGE
            );
        }
        else if (saveEventInputData.getFilePath() == null
                || saveEventInputData
                .getFilePath().isBlank()) {
            this.saveEventOutputBoundary.prepareFailView(
                    MISSING_FILE_MESSAGE
            );
        }
        else {
            this.saveEvent(saveEventInputData);
        }
    }

    private void saveEvent(SaveEventInputData saveEventInputData) {
        try {
            final String eventName = this.saveEventDataAccessInterface.saveEvent(
                                    saveEventInputData.getUsername(),
                                    saveEventInputData.getEventId(), saveEventInputData.getFilePath());
            final SaveEventOutputData outputData = new SaveEventOutputData(eventName);

            this.saveEventOutputBoundary.prepareSuccessView(outputData);
        }
        catch (final SaveEventDataException exception) {
            this.saveEventOutputBoundary.prepareFailView(exception.getMessage());
        }
    }
}

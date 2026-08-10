package use_case.delete_event;

/**
 * The interactor for the delete event use case.
 */
public class DeleteEventInteractor implements DeleteEventInputBoundary {

    private final DeleteEventDataAccessInterface dataAccess;
    private final DeleteEventOutputBoundary presenter;

    public DeleteEventInteractor(DeleteEventDataAccessInterface dataAccess,
                                 DeleteEventOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(DeleteEventInputData deleteEventInputData) {
        final String username = deleteEventInputData.getUsername();

        if (username == null || username.isBlank()) {
            presenter.prepareFailView("You must be signed in to delete an event.");
        }
        else {
            dataAccess.deleteEvent(deleteEventInputData.getEventId());
            presenter.prepareSuccessView(
                    new DeleteEventOutputData(deleteEventInputData.getEventId(), username));
        }
    }
}

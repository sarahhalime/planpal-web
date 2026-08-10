package use_case.load_user_events;

import java.util.List;

import entity.Event;

/**
 * The interactor for the load user events use case.
 */
public class LoadUserEventsInteractor
        implements LoadUserEventsInputBoundary {

    private final LoadUserEventsDataAccessInterface
            loadUserEventsDataAccessInterface;
    private final LoadUserEventsOutputBoundary
            loadUserEventsOutputBoundary;

    public LoadUserEventsInteractor(
            LoadUserEventsDataAccessInterface
                    loadUserEventsDataAccessInterface,
            LoadUserEventsOutputBoundary
                    loadUserEventsOutputBoundary) {
        this.loadUserEventsDataAccessInterface =
                loadUserEventsDataAccessInterface;
        this.loadUserEventsOutputBoundary =
                loadUserEventsOutputBoundary;
    }

    @Override
    public void execute(
            LoadUserEventsInputData loadUserEventsInputData) {
        if (loadUserEventsInputData == null) {
            loadUserEventsOutputBoundary.prepareFailView(
                    "Load user events information is missing.");
        }
        else if (loadUserEventsInputData.getUsername() == null
                || loadUserEventsInputData.getUsername().isBlank()) {
            loadUserEventsOutputBoundary.prepareFailView(
                    "The current user is missing.");
        }
        else {
            try {
                final List<Event> events =
                        loadUserEventsDataAccessInterface.loadEvents(
                                loadUserEventsInputData.getUsername());

                if (events == null) {
                    loadUserEventsOutputBoundary.prepareFailView(
                            "The loaded event data is missing.");
                }
                else {
                    final LoadUserEventsOutputData outputData =
                            new LoadUserEventsOutputData(events);

                    loadUserEventsOutputBoundary.prepareSuccessView(
                            outputData);
                }
            }
            catch (LoadUserEventsDataException exception) {
                loadUserEventsOutputBoundary.prepareFailView(
                        exception.getMessage());
            }
        }
    }
}

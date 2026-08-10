package use_case.event_map;

import java.util.ArrayList;
import java.util.List;

import entity.Activity;
import entity.Event;
import use_case.select_event.SelectEventDataAccessInterface;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/**
 * Loads an event and renders all of its usable locations on one map.
 */
public final class EventMapInteractor implements EventMapInputBoundary {
    private static final String EMPTY_LOCATIONS_MESSAGE =
            "This event does not have any locations to show on the map.";

    private final SelectEventDataAccessInterface eventDataAccessObject;
    private final EventMapGateway mapGateway;
    private final EventMapOutputBoundary presenter;

    /**
     * Creates an event-map interactor.
     *
     * @param eventDataAccessObject event data source
     * @param mapGateway external map service
     * @param presenter output boundary
     */
    public EventMapInteractor(
            SelectEventDataAccessInterface eventDataAccessObject,
            EventMapGateway mapGateway,
            EventMapOutputBoundary presenter) {
        this.eventDataAccessObject = eventDataAccessObject;
        this.mapGateway = mapGateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(EventMapInputData inputData) {
        try {
            final Event event = this.eventDataAccessObject.getEvent(inputData.getEventId());
            final List<EventMapLocation> locations = this.createLocations(event);

            if (locations.isEmpty()) {
                this.presenter.prepareFailView(EMPTY_LOCATIONS_MESSAGE);
            }
            else {
                final EventMapRenderResult result = this.mapGateway.renderMap(locations);
                this.presenter.prepareSuccessView(
                        new EventMapOutputData(result.getPoints())
                );
            }
        }
        catch (WhoOwesWhatDataAccessException | EventMapException exception) {
            this.presenter.prepareFailView(exception.getMessage());
        }
    }

    private List<EventMapLocation> createLocations(Event event) {
        final List<EventMapLocation> locations = new ArrayList<>();
        final String eventAddress = this.clean(event.getEventLocation());

        if (!eventAddress.isBlank()) {
            locations.add(new EventMapLocation(
                    event.getEventName(),
                    eventAddress,
                    event.getStartDate(),
                    event.getStartTime(),
                    true
            ));
        }

        for (final Activity activity : event.getActivityList()) {
            final String activityAddress = this.clean(activity.getLocation());

            if (!activityAddress.isBlank()) {
                locations.add(new EventMapLocation(
                        activity.getActivityName(),
                        activityAddress,
                        this.clean(activity.getDate()),
                        this.clean(activity.getTime()),
                        false
                ));
            }
        }

        return locations;
    }

    private String clean(String value) {
        final String cleaned;

        if (value == null) {
            cleaned = "";
        }
        else {
            cleaned = value.trim();
        }

        return cleaned;
    }
}

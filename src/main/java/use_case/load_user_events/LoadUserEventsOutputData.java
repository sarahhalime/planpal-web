package use_case.load_user_events;

import java.util.List;

import entity.Event;

/**
 * The output data for the load user events use case.
 */
public class LoadUserEventsOutputData {

    private final List<Event> events;

    public LoadUserEventsOutputData(List<Event> events) {
        this.events = List.copyOf(events);
    }

    /**
     * Returns the loaded events.
     *
     * @return the loaded events
     */
    public List<Event> getEvents() {
        return events;
    }
}

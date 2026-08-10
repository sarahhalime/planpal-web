package use_case.event_map;

/**
 * Input boundary for loading an event map.
 */
public interface EventMapInputBoundary {

    /**
     * Loads and renders the selected event's locations.
     *
     * @param inputData selected event data
     */
    void execute(EventMapInputData inputData);
}

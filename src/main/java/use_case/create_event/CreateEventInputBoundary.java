package use_case.create_event;

/**
 * The input boundary for the create event use case.
 */
public interface CreateEventInputBoundary {
    /**
     * Executes the create event use case.
     *
     * @param createEventInputData the information needed to create the event
     */
    void execute(CreateEventInputData createEventInputData);
}

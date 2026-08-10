package use_case.select_event;

/**
 * The input boundary for actions related to selecting an event.
 */
public interface SelectEventInputBoundary {

    /**
     * Executes the select event use case.
     * @param selectEventInputData the input data
     */
    void execute(SelectEventInputData selectEventInputData);
}

package use_case.select_event;

/**
 * The output boundary for the select event use case.
 */
public interface SelectEventOutputBoundary {

    /**
     * Prepares the view after an event was successfully selected.
     * @param outputData the result of the use case
     */
    void prepareSuccessView(SelectEventOutputData outputData);

    /**
     * Prepares the view when the selected event could not be loaded.
     * @param errorMessage the error message
     */
    void prepareFailureView(String errorMessage);
}

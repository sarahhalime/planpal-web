package use_case.create_event;

/**
 * The output boundary for the create event use case.
 */
public interface CreateEventOutputBoundary {
    /**
     * Prepares the successful creation result for presentation.
     *
     * @param createEventOutputData information about the created event
     */
    void prepareSuccessView(CreateEventOutputData createEventOutputData);

    /**
     * Prepares a creation failure for presentation.
     *
     * @param errorMessage the explanation of why creation failed
     */
    default void prepareFailView(String errorMessage) {
        this.prepareFailView(errorMessage, CreateEventErrorField.NONE);
    }

    /**
     * Prepares a creation failure associated with a specific field.
     *
     * @param errorMessage the explanation of why creation failed
     * @param errorField the field associated with the failure
     */
    void prepareFailView(String errorMessage, CreateEventErrorField errorField);
}

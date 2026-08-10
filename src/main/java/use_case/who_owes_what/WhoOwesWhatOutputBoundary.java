package use_case.who_owes_what;

public interface WhoOwesWhatOutputBoundary {

    /**
     * Prepares the ViewModel with the successfully calculated balances.
     * @param whoOwesWhatOutputData the result of the use case.
     */
    void prepareSuccessView(WhoOwesWhatOutputData whoOwesWhatOutputData);

    /**
     * Prepares the ViewModel with an error message when the use case cannot calculate
     * or retrieve the attendee balances.
     * @param errorMessage The error message to display.
     */
    void prepareFailView(String errorMessage);

}

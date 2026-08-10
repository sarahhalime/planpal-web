package use_case.location_insight;

public interface LocationInsightOutputBoundary {

    /**
     * Pass a successful location insight result to the Presenter.
     * Prepares the View Model with the data.
     * @param outputData The result of the use case.
     */
    void prepareSuccessView(LocationInsightOutputData outputData);

    /**
     * Prepares the view model with an error message if the use case can't retrieve
     * location insight data.
     * @param errorMessage The error message that should be presented.
     */
    void prepareFailView(String errorMessage);

}

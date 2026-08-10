package use_case.weather;

// Output boundary: An interface implemented by the Presenter to allow the UCI to send results outwards.
public interface WeatherOutputBoundary {

    /**
     * This method allows the UCI to pass a successful weather result to the Presenter.
     * @param outputData The weather output data to pass.
     */
    void prepareSuccessView(WeatherOutputData outputData);

    /**
     * This method is called by the UCI when the weather use case can't be completed successfully, and it sends
     * an error message to the Presenter.
     * @param errorMessage The error message to display.
     */
    void prepareFailView(String errorMessage);

}

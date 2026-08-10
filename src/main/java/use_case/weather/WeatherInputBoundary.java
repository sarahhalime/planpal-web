package use_case.weather;

public interface WeatherInputBoundary {

    /**
     * Executes the weather use case using the provided input data.
     * @param weatherInputData this contains the event's location and date.
     */
    void execute(WeatherInputData weatherInputData);

}

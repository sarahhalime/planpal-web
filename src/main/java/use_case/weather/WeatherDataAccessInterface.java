package use_case.weather;

import java.time.LocalDate;

import entity.WeatherData;

// DAI: An interface that the DAO implements to give the UCI a decoupled way of accessing data without knowing
// concrete details.
public interface WeatherDataAccessInterface {

    /**
     * Returns a WeatherData object that contains weather data given the location and event date.
     * @param location The location of the event.
     * @param eventDate The date of the event
     * @return A WeatherData object.
     * @throws WeatherDataException In case anything goes wrong.
     */
    WeatherData getWeather(String location, LocalDate eventDate) throws WeatherDataException;

}

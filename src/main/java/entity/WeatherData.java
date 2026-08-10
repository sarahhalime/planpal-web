package entity;

import java.time.LocalDate;
import java.util.List;

public class WeatherData {

    private final String location;
    private final LocalDate requestedDate;
    private final double temperature;
    private final String weatherStatus;
    private final double precipitationProbability;
    private final double windSpeed;
    private final List<ForecastDayWeather> forecast;

    public WeatherData(String location, LocalDate requestedDate, double temperature, String weatherStatus,
                       double precipitationProbability, double windSpeed, List<ForecastDayWeather> forecast) {

        this.location = location;
        this.requestedDate = requestedDate;
        this.temperature = temperature;
        this.weatherStatus = weatherStatus;
        this.precipitationProbability = precipitationProbability;
        this.windSpeed = windSpeed;
        this.forecast = List.copyOf(forecast);

    }

    // Getters

    public String getLocation() {
        return location;
    }

    public LocalDate getRequestedDate() {
        return requestedDate;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getWeatherStatus() {
        return weatherStatus;
    }

    public double getPrecipitationProbability() {
        return precipitationProbability;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public List<ForecastDayWeather> getForecast() {
        return forecast;
    }

}

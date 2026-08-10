package use_case.weather;

import java.util.List;

// Output Data is an object created by the UCI to send the results of the use case to the presenter.
public class WeatherOutputData {

    private String location;
    private double temperature;
    private String weatherStatus;
    private double precipitationProbability;
    private double windSpeed;
    private List<ForecastDayOutputData> forecast;

    public WeatherOutputData(String location, double temperature, String weatherStatus,
                             double precipitationProbability, double windSpeed,
                             List<ForecastDayOutputData> forecast) {

        this.location = location;
        this.temperature = temperature;
        this.weatherStatus = weatherStatus;
        this.precipitationProbability = precipitationProbability;
        this.windSpeed = windSpeed;
        this.forecast = List.copyOf(forecast);

    }

    // Getters and setters

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getWeatherStatus() {
        return weatherStatus;
    }

    public void setWeatherStatus(String weatherStatus) {
        this.weatherStatus = weatherStatus;
    }

    public double getPrecipitationProbability() {
        return precipitationProbability;
    }

    public void setPrecipitationProbability(double precipitationProbability) {
        this.precipitationProbability = precipitationProbability;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public List<ForecastDayOutputData> getForecast() {
        return forecast;
    }

    public void setForecast(List<ForecastDayOutputData> forecast) {
        this.forecast = forecast;
    }

}

package entity;

import java.time.LocalDate;

public class ForecastDayWeather {

    private final LocalDate date;
    private final double temperature;
    private final String weatherStatus;

    public ForecastDayWeather(LocalDate date, double temperature, String weatherStatus) {
        this.date = date;
        this.temperature = temperature;
        this.weatherStatus = weatherStatus;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getWeatherStatus() {
        return weatherStatus;
    }

}

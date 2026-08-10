package use_case.weather;

import java.time.LocalDate;

public class ForecastDayOutputData {

    private LocalDate date;
    private double tempAtDay;
    private String weatherStatusAtDay;

    public ForecastDayOutputData(LocalDate date, double tempAtDay, String weatherStatusAtDay) {
        this.date = date;
        this.tempAtDay = tempAtDay;
        this.weatherStatusAtDay = weatherStatusAtDay;
    }

    // Getters and setters

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getTempAtDay() {
        return tempAtDay;
    }

    public void setTempAtDay(double tempAtDay) {
        this.tempAtDay = tempAtDay;
    }

    public String getWeatherStatusAtDay() {
        return weatherStatusAtDay;
    }

    public void setWeatherStatusAtDay(String weatherStatusAtDay) {
        this.weatherStatusAtDay = weatherStatusAtDay;
    }

}

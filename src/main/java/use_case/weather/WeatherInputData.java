package use_case.weather;

import java.time.LocalDate;

// Input data: An Input Data object will be created by the Controller, and the input data essentially be in the
// most optimal format for the UCI.

public class WeatherInputData {

    // We are going to want the weather information for this event's location and date

    private final String location;
    private final LocalDate eventDate;

    public WeatherInputData(String location, LocalDate eventDate) {
        this.location = location;
        this.eventDate = eventDate;
    }

    // Getters

    public String getLocation() {
        return location;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

}

package use_case.weather;

import java.util.ArrayList;
import java.util.List;

import entity.ForecastDayWeather;
import entity.WeatherData;

public class WeatherInteractor implements WeatherInputBoundary {

    private final WeatherDataAccessInterface weatherDataAccessInterface;
    private final WeatherOutputBoundary weatherOutputBoundary;

    public WeatherInteractor(WeatherDataAccessInterface weatherDataAccessInterface,
                             WeatherOutputBoundary weatherOutputBoundary) {

        this.weatherDataAccessInterface = weatherDataAccessInterface;
        this.weatherOutputBoundary = weatherOutputBoundary;

    }

    @Override
    public void execute(WeatherInputData weatherInputData) {

        if (weatherInputData == null) {
            weatherOutputBoundary.prepareFailView("Weather request information is missing.");
        }
        else if (weatherInputData.getLocation() == null || weatherInputData.getLocation().isBlank()) {

            weatherOutputBoundary.prepareFailView("An event location is required.");
        }
        else if (weatherInputData.getEventDate() == null) {
            weatherOutputBoundary.prepareFailView("An event date is required.");
        }
        else {
            try {
                // Ask the DAI for the weather data.
                final WeatherData weatherData = weatherDataAccessInterface.getWeather(weatherInputData.getLocation(),
                        weatherInputData.getEventDate());

                // Convert it to WeatherOutputData
                final List<ForecastDayOutputData> fdod = new ArrayList<>();

                for (ForecastDayWeather forecastDayWeather : weatherData.getForecast()) {
                    fdod.add(new ForecastDayOutputData(forecastDayWeather.getDate(),
                            forecastDayWeather.getTemperature(),
                            forecastDayWeather.getWeatherStatus()));
                }

                final WeatherOutputData weatherOutputData = new WeatherOutputData(weatherData.getLocation(),
                        weatherData.getTemperature(), weatherData.getWeatherStatus(),
                        weatherData.getPrecipitationProbability(), weatherData.getWindSpeed(), fdod);

                // Call prepareSuccessView
                weatherOutputBoundary.prepareSuccessView(weatherOutputData);
            }
            catch (WeatherDataException exception) {
                weatherOutputBoundary.prepareFailView(exception.getMessage());
            }
        }
    }
}

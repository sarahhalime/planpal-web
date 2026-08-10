package data_access;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.ForecastDayWeather;
import entity.WeatherData;
import use_case.weather.WeatherDataAccessInterface;
import use_case.weather.WeatherDataException;

public class WeatherDao implements WeatherDataAccessInterface {

    private static final String UNKNOWN_STATUS = "Unknown";
    private static final String RESULTS_KEY = "results";
    private static final int HTTP_OK = 200;
    private static final int FORECAST_DAY_COUNT = 3;
    private static final int CLEAR_CODE_MAX = 3;
    private static final int FOG_CODE_MIN = 45;
    private static final int FOG_CODE_MAX = 48;
    private static final int PRECIPITATION_CODE_MAX = 77;
    private static final int DRIZZLE_CODE_MIN = 51;
    private static final int DRIZZLE_CODE_MAX = 55;
    private static final int RAIN_CODE_MIN = 61;
    private static final int RAIN_CODE_MAX = 65;
    private static final int SNOW_CODE_MIN = 71;
    private static final int SNOW_CODE_MAX = 75;
    private static final int SHOWER_CODE_MIN = 80;
    private static final int SHOWER_CODE_MAX = 82;
    private static final int STORM_CODE_MAX = 99;
    private static final int SNOW_SHOWER_CODE_MIN = 85;
    private static final int SNOW_SHOWER_CODE_MAX = 86;
    private static final int THUNDERSTORM_CODE = 95;
    private static final int HAIL_CODE_MIN = 96;

    @Override
    public WeatherData getWeather(String location, LocalDate eventDate) throws WeatherDataException {

        try {

            final double[] coordinates = getCoordinates(location);
            final double latitude = coordinates[0];
            final double longitude = coordinates[1];

            final JSONObject daily = this.fetchDailyForecast(latitude, longitude, eventDate);

            final JSONArray dates = daily.getJSONArray("time");
            final JSONArray weatherCodes = daily.getJSONArray("weather_code");
            final JSONArray temperatures = daily.getJSONArray("temperature_2m_max");
            final JSONArray precipitation = daily.getJSONArray("precipitation_probability_max");
            final JSONArray windSpeeds = daily.getJSONArray("wind_speed_10m_max");

            // Find the specific entry in the JSON array that corresponds to the event date.
            int eventDateIndex = -1;

            for (int index = 0; index < dates.length(); index++) {
                final LocalDate date = LocalDate.parse(dates.getString(index));

                if (date.equals(eventDate)) {
                    eventDateIndex = index;
                    break;
                }
            }

            if (eventDateIndex == -1) {
                throw new WeatherDataException("No forecast available for that date.");
            }

            // Now that we have the index, we can get the specific values from the array at that index.
            final double temperature = temperatures.getDouble(eventDateIndex);
            final double precipitationProbability = precipitation.getDouble(eventDateIndex);
            final double windSpeed = windSpeeds.getDouble(eventDateIndex);
            final int weatherCode = weatherCodes.getInt(eventDateIndex);

            final List<ForecastDayWeather> forecastDayWeatherList =
                    this.buildForecastDays(dates, temperatures, weatherCodes, eventDateIndex);

            return new WeatherData(
                    location,
                    eventDate,
                    temperature,
                    weatherCodeToStatus(weatherCode),
                    precipitationProbability,
                    windSpeed,
                    forecastDayWeatherList
            );

        }

        catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new WeatherDataException("The weather request was interrupted.");
        }

        catch (IOException exception) {
            throw new WeatherDataException("Unable to connect to the weather API.");
        }

        catch (JSONException | DateTimeParseException exception) {
            throw new WeatherDataException("The weather service returned unexpected data.");
        }

    }

    private List<ForecastDayWeather> buildForecastDays(JSONArray dates, JSONArray temperatures,
                                                       JSONArray weatherCodes, int eventDateIndex)
            throws WeatherDataException {

        if (eventDateIndex + 2 >= dates.length()) {
            throw new WeatherDataException("Not enough forecast data available.");
        }

        final List<ForecastDayWeather> forecastDayWeatherList = new ArrayList<>();
        for (int index = 0; index < FORECAST_DAY_COUNT; index++) {
            final int forecastIndex = eventDateIndex + index;
            final LocalDate forecastDate = LocalDate.parse(dates.getString(forecastIndex));

            forecastDayWeatherList.add(new ForecastDayWeather(forecastDate,
                    temperatures.getDouble(forecastIndex),
                    weatherCodeToStatus(weatherCodes.getInt(forecastIndex))));
        }
        return forecastDayWeatherList;
    }

    private JSONObject fetchDailyForecast(double latitude, double longitude, LocalDate eventDate)
            throws IOException, InterruptedException, WeatherDataException {

        final LocalDate endDate = eventDate.plusDays(2);
        final String apiUrl = "https://api.open-meteo.com/v1/forecast"
                + "?latitude=" + latitude
                + "&longitude=" + longitude
                + "&daily=weather_code,temperature_2m_max,"
                + "precipitation_probability_max,wind_speed_10m_max"
                + "&start_date=" + eventDate
                + "&end_date=" + endDate
                + "&timezone=auto";

        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).GET().build();
        final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HTTP_OK) {
            throw new WeatherDataException("Weather API request failed.");
        }

        return new JSONObject(response.body()).getJSONObject("daily");
    }

    private String weatherCodeToStatus(int weatherCode) {
        String weatherStatus = UNKNOWN_STATUS;

        if (weatherCode >= 0 && weatherCode <= CLEAR_CODE_MAX) {
            weatherStatus = interpretClearAndCloudy(weatherCode);
        }
        else if (weatherCode >= FOG_CODE_MIN && weatherCode <= PRECIPITATION_CODE_MAX) {
            weatherStatus = interpretFogRainAndSnow(weatherCode);
        }
        else if (weatherCode >= SHOWER_CODE_MIN && weatherCode <= STORM_CODE_MAX) {
            weatherStatus = interpretShowersAndStorms(weatherCode);
        }

        return weatherStatus;
    }

    private String interpretClearAndCloudy(int weatherCode) {
        String weatherStatus = "Overcast";

        if (weatherCode == 0) {
            weatherStatus = "Clear";
        }
        else if (weatherCode == 1) {
            weatherStatus = "Mainly Clear";
        }
        else if (weatherCode == 2) {
            weatherStatus = "Partly Cloudy";
        }

        return weatherStatus;
    }

    private String interpretFogRainAndSnow(int weatherCode) {
        String weatherStatus = UNKNOWN_STATUS;

        if (weatherCode == FOG_CODE_MIN || weatherCode == FOG_CODE_MAX) {
            weatherStatus = "Fog";
        }
        else if (weatherCode >= DRIZZLE_CODE_MIN && weatherCode <= DRIZZLE_CODE_MAX) {
            weatherStatus = "Drizzle";
        }
        else if (weatherCode >= RAIN_CODE_MIN && weatherCode <= RAIN_CODE_MAX) {
            weatherStatus = "Rain";
        }
        else if (weatherCode >= SNOW_CODE_MIN && weatherCode <= SNOW_CODE_MAX) {
            weatherStatus = "Snow";
        }

        return weatherStatus;
    }

    private String interpretShowersAndStorms(int weatherCode) {
        String weatherStatus = UNKNOWN_STATUS;

        if (weatherCode >= SHOWER_CODE_MIN && weatherCode <= SHOWER_CODE_MAX) {
            weatherStatus = "Rain Showers";
        }
        else if (weatherCode == SNOW_SHOWER_CODE_MIN || weatherCode == SNOW_SHOWER_CODE_MAX) {
            weatherStatus = "Snow Showers";
        }
        else if (weatherCode == THUNDERSTORM_CODE) {
            weatherStatus = "Thunderstorm";
        }
        else if (weatherCode == HAIL_CODE_MIN || weatherCode == STORM_CODE_MAX) {
            weatherStatus = "Thunderstorm with Hail";
        }

        return weatherStatus;
    }

    private double[] getCoordinates(String location)
            throws IOException, InterruptedException, WeatherDataException {

        final List<String> locationCandidates =
                this.createLocationCandidates(location);
        double[] coordinates = null;

        for (final String locationCandidate : locationCandidates) {
            coordinates = this.searchCoordinates(locationCandidate);

            if (coordinates != null) {
                break;
            }
        }

        if (coordinates == null) {
            throw new WeatherDataException("Location could not be found.");
        }

        return coordinates;
    }

    private List<String> createLocationCandidates(String location) {
        final List<String> locationCandidates = new ArrayList<>();
        final String cleanedLocation = location.strip();

        this.addLocationCandidate(locationCandidates, cleanedLocation);

        final String withoutParentheses = cleanedLocation
                .replaceAll("\\s*\\([^)]*\\)", "")
                .strip();
        this.addLocationCandidate(locationCandidates, withoutParentheses);

        final String[] addressParts = cleanedLocation.split(",");

        for (int index = 0; index < addressParts.length; index++) {
            final String addressPart = addressParts[index].strip();

            if (addressPart.length() > 2
                    && !this.isCountryOnlyCandidate(addressPart)) {
                this.addLocationCandidate(locationCandidates, addressPart);
            }
        }

        return locationCandidates;
    }

    private void addLocationCandidate(
            List<String> locationCandidates,
            String locationCandidate) {

        if (!locationCandidate.isBlank()
                && !locationCandidates.contains(locationCandidate)) {
            locationCandidates.add(locationCandidate);
        }
    }

    private boolean isCountryOnlyCandidate(String locationCandidate) {
        return "Canada".equalsIgnoreCase(locationCandidate)
                || "United States".equalsIgnoreCase(locationCandidate)
                || "USA".equalsIgnoreCase(locationCandidate);
    }

    private double[] searchCoordinates(String location)
            throws IOException, InterruptedException, WeatherDataException {

        final String encodedLocation =
                URLEncoder.encode(location, StandardCharsets.UTF_8);

        final String geocodingUrl =
                "https://geocoding-api.open-meteo.com/v1/search"
                        + "?name=" + encodedLocation
                        + "&count=1";

        final HttpClient client = HttpClient.newHttpClient();

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(geocodingUrl))
                .GET()
                .build();

        final HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HTTP_OK) {
            throw new WeatherDataException("Location search failed.");
        }

        final JSONObject root = new JSONObject(response.body());
        double[] coordinates = null;

        if (root.has(RESULTS_KEY)
                && !root.getJSONArray(RESULTS_KEY).isEmpty()) {
            final JSONObject firstResult =
                    root.getJSONArray(RESULTS_KEY).getJSONObject(0);

            coordinates = new double[] {
                    firstResult.getDouble("latitude"),
                    firstResult.getDouble("longitude"),
            };
        }

        return coordinates;
    }

}

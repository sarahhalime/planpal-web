package data_access;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import use_case.event_map.EventMapException;
import use_case.event_map.EventMapGateway;
import use_case.event_map.EventMapLocation;
import use_case.event_map.EventMapRenderResult;
import use_case.event_map.EventMapRenderedPoint;

/**
 * Resolves event-map addresses to coordinates for the interactive OpenStreetMap view.
 */
public final class OpenStreetMapEventMapGateway implements EventMapGateway {
    private static final String PLACES_TEXT_SEARCH_URL =
            "https://places.googleapis.com/v1/places:searchText";
    private static final String MISSING_KEY_MESSAGE =
            "Google Places is not configured. Add the existing Google Maps demo key to view the event map.";
    private static final String GEOCODING_FAILURE_MESSAGE =
            "The saved locations could not be resolved. Check that Places API is enabled for the demo key.";
    private static final String NO_GEOCODED_LOCATIONS_MESSAGE =
            "None of the saved event locations could be found on the map.";
    private static final int REQUEST_TIMEOUT_SECONDS = 12;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
            .build();
    private final String apiKey;

    /**
     * Creates an event-map geocoding gateway.
     *
     * @param apiKey existing Google Places key used to resolve saved addresses
     */
    public OpenStreetMapEventMapGateway(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public EventMapRenderResult renderMap(List<EventMapLocation> locations) throws EventMapException {
        this.requireApiKey();
        final List<EventMapRenderedPoint> points = new ArrayList<>();

        for (final EventMapLocation location : locations) {
            final Coordinate coordinate = this.findCoordinate(location.getAddress());

            if (coordinate != null) {
                points.add(new EventMapRenderedPoint(
                        location,
                        coordinate.latitude,
                        coordinate.longitude
                ));
            }
        }

        if (points.isEmpty()) {
            throw new EventMapException(NO_GEOCODED_LOCATIONS_MESSAGE);
        }

        return new EventMapRenderResult(points);
    }

    private void requireApiKey() throws EventMapException {
        if (this.apiKey == null || this.apiKey.isBlank()) {
            throw new EventMapException(MISSING_KEY_MESSAGE);
        }
    }

    private Coordinate findCoordinate(String address) throws EventMapException {
        final JSONObject requestBody = new JSONObject();
        requestBody.put("textQuery", address);
        requestBody.put("maxResultCount", 1);
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PLACES_TEXT_SEARCH_URL))
                .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
                .header("Content-Type", "application/json")
                .header("X-Goog-Api-Key", this.apiKey)
                .header("X-Goog-FieldMask", "places.location")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();
        final Coordinate coordinate;

        try {
            final HttpResponse<String> response =
                    this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new EventMapException(GEOCODING_FAILURE_MESSAGE);
            }
            coordinate = this.parseCoordinate(response.body());
        }
        catch (IOException exception) {
            throw new EventMapException(GEOCODING_FAILURE_MESSAGE);
        }
        catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new EventMapException(GEOCODING_FAILURE_MESSAGE);
        }

        return coordinate;
    }

    private Coordinate parseCoordinate(String responseBody) {
        final JSONObject response = new JSONObject(responseBody);
        final JSONArray places = response.optJSONArray("places");
        final Coordinate coordinate;

        if (places == null || places.length() == 0) {
            coordinate = null;
        }
        else {
            final JSONObject location = places.getJSONObject(0).getJSONObject("location");
            coordinate = new Coordinate(
                    location.getDouble("latitude"),
                    location.getDouble("longitude")
            );
        }

        return coordinate;
    }

    /**
     * Stores one resolved coordinate.
     */
    private static final class Coordinate {
        private final double latitude;
        private final double longitude;

        private Coordinate(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}

package data_access;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import use_case.itinerary.ItineraryTravelEstimate;
import use_case.itinerary.ItineraryTravelException;
import use_case.itinerary.ItineraryTravelFailure;
import use_case.itinerary.ItineraryTravelGateway;

/**
 * Uses Google Places for geocoding and Valhalla/OpenStreetMap for real travel-time routing.
 */
public final class ValhallaItineraryTravelGateway implements ItineraryTravelGateway {
    private static final String PLACES_TEXT_SEARCH_URL =
            "https://places.googleapis.com/v1/places:searchText";
    private static final String VALHALLA_ROUTE_URL =
            "https://valhalla1.openstreetmap.de/route";
    private static final String CLIENT_ID = "PlanPal-UofT-CSC207";
    private static final String AUTO_COSTING = "auto";
    private static final String PEDESTRIAN_COSTING = "pedestrian";
    private static final String LOCATION_FAILURE_MESSAGE = "Location couldn't be resolved";
    private static final String ROUTE_FAILURE_MESSAGE = "No route found";
    private static final String SERVICE_FAILURE_MESSAGE = "Routing service unavailable";
    private static final int REQUEST_TIMEOUT_SECONDS = 12;
    private static final int SECONDS_PER_MINUTE = 60;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
            .build();
    private final Map<String, Coordinate> coordinateCache = new HashMap<>();
    private final Map<String, ItineraryTravelEstimate> routeCache = new HashMap<>();
    private final String googlePlacesApiKey;

    /**
     * Creates the itinerary travel gateway.
     *
     * @param googlePlacesApiKey Google Places key used to resolve saved locations
     */
    public ValhallaItineraryTravelGateway(String googlePlacesApiKey) {
        this.googlePlacesApiKey = googlePlacesApiKey;
    }

    @Override
    public ItineraryTravelEstimate estimateTravel(String origin, String destination)
            throws ItineraryTravelException {
        final String cacheKey = origin + "\n" + destination;
        ItineraryTravelEstimate estimate = this.routeCache.get(cacheKey);

        if (estimate == null) {
            final Coordinate originCoordinate = this.coordinateFor(origin);
            final Coordinate destinationCoordinate = this.coordinateFor(destination);
            final RouteResult driving = this.route(
                    originCoordinate,
                    destinationCoordinate,
                    AUTO_COSTING
            );
            final RouteResult walking = this.route(
                    originCoordinate,
                    destinationCoordinate,
                    PEDESTRIAN_COSTING
            );

            estimate = new ItineraryTravelEstimate(driving.minutes, walking.minutes);
            if (!estimate.hasAnyTime()) {
                throw this.routingFailure(driving.failure, walking.failure);
            }
            this.routeCache.put(cacheKey, estimate);
        }
        return estimate;
    }

    private Coordinate coordinateFor(String location) throws ItineraryTravelException {
        Coordinate coordinate = this.coordinateCache.get(location);

        if (coordinate == null) {
            coordinate = this.geocode(location);
            this.coordinateCache.put(location, coordinate);
        }
        return coordinate;
    }

    private Coordinate geocode(String locationText) throws ItineraryTravelException {
        this.requireApiKey();
        final JSONObject requestBody = new JSONObject();
        requestBody.put("textQuery", locationText);
        requestBody.put("maxResultCount", 1);

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PLACES_TEXT_SEARCH_URL))
                .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
                .header("Content-Type", "application/json")
                .header("X-Goog-Api-Key", this.googlePlacesApiKey)
                .header("X-Goog-FieldMask", "places.location")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        final HttpResponse<String> response = this.sendGeocode(request);
        final Coordinate coordinate;

        if (response.statusCode() >= HttpURLConnection.HTTP_INTERNAL_ERROR) {
            throw this.serviceFailure();
        }
        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw this.locationFailure();
        }
        coordinate = this.parseCoordinate(response.body());
        if (coordinate == null) {
            throw this.locationFailure();
        }
        return coordinate;
    }

    private RouteResult route(Coordinate origin, Coordinate destination, String costing) {
        RouteResult result;
        result = RouteResult.serviceUnavailable();
        final JSONObject requestBody = new JSONObject();
        final JSONArray locations = new JSONArray();

        locations.put(this.locationJson(origin));
        locations.put(this.locationJson(destination));
        requestBody.put("locations", locations);
        requestBody.put("costing", costing);
        requestBody.put("units", "kilometers");

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VALHALLA_ROUTE_URL))
                .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
                .header("Content-Type", "application/json")
                .header("X-Client-Id", CLIENT_ID)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        try {
            final HttpResponse<String> response =
                    this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                final int minutes = this.parseRouteMinutes(response.body());

                if (minutes >= 0) {
                    result = RouteResult.success(minutes);
                }
                else {
                    result = RouteResult.noRoute();
                }
            }
            else if (response.statusCode() < HttpURLConnection.HTTP_INTERNAL_ERROR) {
                result = RouteResult.noRoute();
            }
        }
        catch (IOException exception) {
            result = RouteResult.serviceUnavailable();
        }
        catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            result = RouteResult.serviceUnavailable();
        }
        return result;
    }

    private HttpResponse<String> sendGeocode(HttpRequest request)
            throws ItineraryTravelException {
        final HttpResponse<String> response;

        try {
            response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch (IOException exception) {
            throw this.serviceFailure();
        }
        catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw this.serviceFailure();
        }
        return response;
    }

    private Coordinate parseCoordinate(String responseBody) {
        final JSONObject response = new JSONObject(responseBody);
        final JSONArray places = response.optJSONArray("places");
        Coordinate coordinate = null;

        if (places != null && places.length() > 0) {
            final JSONObject location = places.getJSONObject(0).getJSONObject("location");
            coordinate = new Coordinate(
                    location.getDouble("latitude"),
                    location.getDouble("longitude")
            );
        }
        return coordinate;
    }

    private int parseRouteMinutes(String responseBody) {
        final JSONObject response = new JSONObject(responseBody);
        final JSONObject trip = response.optJSONObject("trip");
        int minutes = ItineraryTravelEstimate.UNAVAILABLE_MINUTES;

        if (trip != null) {
            final JSONObject summary = trip.optJSONObject("summary");

            if (summary != null && summary.has("time")) {
                final double seconds = summary.getDouble("time");
                minutes = (int) Math.ceil(seconds / SECONDS_PER_MINUTE);
            }
        }
        return minutes;
    }

    private JSONObject locationJson(Coordinate coordinate) {
        final JSONObject location = new JSONObject();

        location.put("lat", coordinate.latitude);
        location.put("lon", coordinate.longitude);
        return location;
    }

    private ItineraryTravelException routingFailure(
            ItineraryTravelFailure drivingFailure,
            ItineraryTravelFailure walkingFailure) {
        final ItineraryTravelException exception;

        if (drivingFailure == ItineraryTravelFailure.SERVICE_UNAVAILABLE
                || walkingFailure == ItineraryTravelFailure.SERVICE_UNAVAILABLE) {
            exception = this.serviceFailure();
        }
        else {
            exception = new ItineraryTravelException(
                    ROUTE_FAILURE_MESSAGE,
                    ItineraryTravelFailure.NO_ROUTE
            );
        }
        return exception;
    }

    private ItineraryTravelException locationFailure() {
        return new ItineraryTravelException(
                LOCATION_FAILURE_MESSAGE,
                ItineraryTravelFailure.LOCATION_NOT_RESOLVED
        );
    }

    private ItineraryTravelException serviceFailure() {
        return new ItineraryTravelException(
                SERVICE_FAILURE_MESSAGE,
                ItineraryTravelFailure.SERVICE_UNAVAILABLE
        );
    }

    private void requireApiKey() throws ItineraryTravelException {
        if (this.googlePlacesApiKey == null || this.googlePlacesApiKey.isBlank()) {
            throw this.serviceFailure();
        }
    }

    /**
     * Stores a geocoded coordinate.
     */
    private static final class Coordinate {
        private final double latitude;
        private final double longitude;

        private Coordinate(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    /**
     * Stores one routing-mode result without allowing one failed mode to hide another.
     */
    private static final class RouteResult {
        private final int minutes;
        private final ItineraryTravelFailure failure;

        private RouteResult(int minutes, ItineraryTravelFailure failure) {
            this.minutes = minutes;
            this.failure = failure;
        }

        private static RouteResult success(int minutes) {
            return new RouteResult(minutes, ItineraryTravelFailure.NONE);
        }

        private static RouteResult noRoute() {
            return new RouteResult(
                    ItineraryTravelEstimate.UNAVAILABLE_MINUTES,
                    ItineraryTravelFailure.NO_ROUTE
            );
        }

        private static RouteResult serviceUnavailable() {
            return new RouteResult(
                    ItineraryTravelEstimate.UNAVAILABLE_MINUTES,
                    ItineraryTravelFailure.SERVICE_UNAVAILABLE
            );
        }
    }
}

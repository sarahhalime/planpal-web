package data_access;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import use_case.event_map.EventMapException;
import use_case.event_map.EventMapGateway;
import use_case.event_map.EventMapLocation;
import use_case.event_map.EventMapRenderResult;
import use_case.event_map.EventMapRenderedPoint;

/**
 * Places an event's locations on the map using Nominatim, OpenStreetMap's own geocoder.
 *
 * <p>The Google gateway refuses every request unless billing is enabled on the Cloud project.
 * Nominatim needs no key, and unlike a place-name search it resolves venues and streets, which
 * is what an activity's location usually is.</p>
 *
 * <p>Nominatim asks callers for an identifying user agent and no more than one request a
 * second, so this waits between lookups and caches what it has already resolved.</p>
 */
public final class NominatimEventMapGateway implements EventMapGateway {

    private static final String SEARCH_URL =
            "https://nominatim.openstreetmap.org/search?format=json&limit=1&q=";
    private static final String USER_AGENT = "PlanPal/1.0 (CSC207 course project)";
    private static final int TIMEOUT_SECONDS = 12;
    private static final long MINIMUM_GAP_MILLIS = 1100;

    private long lastRequestAt;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
            .build();

    /** Place names repeat across an event's activities, so each is looked up once. */
    private final Map<String, double[]> resolved = new HashMap<>();

    @Override
    public EventMapRenderResult renderMap(List<EventMapLocation> locations)
            throws EventMapException {

        final List<EventMapRenderedPoint> points = new ArrayList<>();
        final String city = cityOf(locations);

        for (final EventMapLocation location : locations) {
            double[] coordinates = null;

            // An activity's address is usually just a place name such as "Old Port", which
            // matches somewhere in the wrong country on its own. Asking for it alongside the
            // event's own location pins it to the right city first.
            if (city != null && !location.isEventLocation()) {
                coordinates = this.coordinatesFor(location.getAddress() + ", " + city);

                // A bare retry finds same-named places anywhere in the world, which puts a
                // Montreal brunch spot in London. Fall back to the event's own location so
                // the point at least sits in the right city.
                if (coordinates == null) {
                    coordinates = this.coordinatesFor(city);
                }
            }
            else {
                coordinates = this.coordinatesFor(location.getAddress());
            }
            if (coordinates != null) {
                points.add(new EventMapRenderedPoint(location, coordinates[0], coordinates[1]));
            }
        }

        if (points.isEmpty()) {
            throw new EventMapException("None of the saved locations could be found on the map.");
        }
        return new EventMapRenderResult(points);
    }

    /**
     * Returns the event's own location, which is the city the activities sit in.
     *
     * @param locations everything being placed on the map
     * @return the event's address, or null when there is not one
     */
    private static String cityOf(List<EventMapLocation> locations) {
        String city = null;

        for (final EventMapLocation location : locations) {
            if (location.isEventLocation()
                    && location.getAddress() != null && !location.getAddress().isBlank()) {
                city = location.getAddress().trim();
            }
        }
        return city;
    }

    /**
     * Resolves a place name to latitude and longitude.
     *
     * @param address the place to look up
     * @return the coordinates, or null when the place cannot be found
     */
    private double[] coordinatesFor(String address) {
        double[] coordinates = null;

        if (address != null && !address.isBlank()) {
            final String key = address.trim();

            if (this.resolved.containsKey(key)) {
                coordinates = this.resolved.get(key);
            }
            else {
                coordinates = this.lookUp(key);
                this.resolved.put(key, coordinates);
            }
        }
        return coordinates;
    }

    /**
     * Keeps to Nominatim's one-request-a-second usage policy.
     */
    private void waitForNextSlot() throws InterruptedException {
        final long since = System.currentTimeMillis() - this.lastRequestAt;

        if (since < MINIMUM_GAP_MILLIS) {
            Thread.sleep(MINIMUM_GAP_MILLIS - since);
        }
        this.lastRequestAt = System.currentTimeMillis();
    }

    private double[] lookUp(String address) {
        double[] coordinates = null;

        try {
            this.waitForNextSlot();

            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SEARCH_URL + URLEncoder.encode(address, StandardCharsets.UTF_8)))
                    .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                    .header("User-Agent", USER_AGENT)
                    .GET()
                    .build();
            final HttpResponse<String> response =
                    this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            final JSONArray results = new JSONArray(response.body());

            if (!results.isEmpty()) {
                final JSONObject first = results.getJSONObject(0);
                coordinates = new double[] {
                    Double.parseDouble(first.getString("lat")),
                    Double.parseDouble(first.getString("lon"))
                };
            }
        }
        catch (final IOException | RuntimeException exception) {
            coordinates = null;
        }
        catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            coordinates = null;
        }
        return coordinates;
    }
}

package use_case.event_map;

/**
 * Contains a geocoded event-map point.
 */
public final class EventMapRenderedPoint {
    private final EventMapLocation location;
    private final double latitude;
    private final double longitude;

    /**
     * Creates a geocoded map point.
     *
     * @param location location metadata
     * @param latitude point latitude
     * @param longitude point longitude
     */
    public EventMapRenderedPoint(EventMapLocation location, double latitude, double longitude) {
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Returns the location metadata.
     * @return location metadata
     */
    public EventMapLocation getLocation() {
        return this.location;
    }

    /**
     * Returns the latitude.
     * @return latitude
     */
    public double getLatitude() {
        return this.latitude;
    }

    /**
     * Returns the longitude.
     * @return longitude
     */
    public double getLongitude() {
        return this.longitude;
    }
}

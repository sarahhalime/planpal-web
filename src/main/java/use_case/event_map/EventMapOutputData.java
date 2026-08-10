package use_case.event_map;

import java.util.List;

/**
 * Output returned after event locations are geocoded.
 */
public final class EventMapOutputData {
    private final List<EventMapRenderedPoint> points;

    /**
     * Creates map output data.
     *
     * @param points geocoded map points
     */
    public EventMapOutputData(List<EventMapRenderedPoint> points) {
        this.points = List.copyOf(points);
    }

    /**
     * Returns the geocoded map points.
     * @return map points
     */
    public List<EventMapRenderedPoint> getPoints() {
        return this.points;
    }
}

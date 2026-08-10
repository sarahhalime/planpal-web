package use_case.event_map;

import java.util.List;

/**
 * Result returned after event locations are geocoded.
 */
public final class EventMapRenderResult {
    private final List<EventMapRenderedPoint> points;

    /**
     * Creates a geocoded map result.
     *
     * @param points geocoded map points
     */
    public EventMapRenderResult(List<EventMapRenderedPoint> points) {
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

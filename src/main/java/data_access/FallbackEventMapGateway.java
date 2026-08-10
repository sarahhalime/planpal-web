package data_access;

import java.util.List;

import use_case.event_map.EventMapException;
import use_case.event_map.EventMapGateway;
import use_case.event_map.EventMapLocation;
import use_case.event_map.EventMapRenderResult;

/**
 * Tries one map gateway and falls back to another when it cannot answer.
 *
 * <p>Google is the better source when it is available, but it refuses every request unless
 * billing is enabled on the Cloud project. Rather than make the map depend on that being set
 * up, this tries Google first and quietly falls back to the keyless service.</p>
 */
public final class FallbackEventMapGateway implements EventMapGateway {

    private final EventMapGateway preferred;
    private final EventMapGateway fallback;

    /**
     * Creates a gateway that prefers one source and falls back to another.
     *
     * @param preferred tried first
     * @param fallback used when the preferred one fails
     */
    public FallbackEventMapGateway(EventMapGateway preferred, EventMapGateway fallback) {
        this.preferred = preferred;
        this.fallback = fallback;
    }

    @Override
    public EventMapRenderResult renderMap(List<EventMapLocation> locations)
            throws EventMapException {

        EventMapRenderResult result;

        try {
            result = this.preferred.renderMap(locations);
        }
        catch (final EventMapException | RuntimeException exception) {
            result = this.fallback.renderMap(locations);
        }
        return result;
    }
}

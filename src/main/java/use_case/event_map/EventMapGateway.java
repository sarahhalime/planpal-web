package use_case.event_map;

import java.util.List;

/**
 * External map service used to geocode and render event locations.
 */
public interface EventMapGateway {

    /**
     * Geocodes and renders the supplied locations.
     *
     * @param locations locations to render
     * @return rendered map data
     * @throws EventMapException when the map service cannot create a map
     */
    EventMapRenderResult renderMap(List<EventMapLocation> locations) throws EventMapException;
}

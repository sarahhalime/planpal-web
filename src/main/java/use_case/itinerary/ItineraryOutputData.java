package use_case.itinerary;

import java.util.ArrayList;
import java.util.List;

/**
 * Chronologically ordered itinerary data.
 */
public final class ItineraryOutputData {
    private final List<ItineraryItemOutputData> items;

    /**
     * Creates itinerary output data.
     *
     * @param items ordered itinerary items
     */
    public ItineraryOutputData(List<ItineraryItemOutputData> items) {
        this.items = new ArrayList<>(items);
    }

    /**
     * Returns the ordered itinerary items.
     *
     * @return itinerary items
     */
    public List<ItineraryItemOutputData> getItems() {
        return new ArrayList<>(this.items);
    }
}

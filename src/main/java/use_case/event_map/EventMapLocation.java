package use_case.event_map;

/**
 * Describes one event or activity location that should appear on the event map.
 */
public final class EventMapLocation {
    private final String title;
    private final String address;
    private final String date;
    private final String time;
    private final boolean eventLocation;

    /**
     * Creates a mappable event location.
     *
     * @param title display title
     * @param address address to geocode
     * @param date optional date text
     * @param time optional time text
     * @param eventLocation whether this is the event's main location
     */
    public EventMapLocation(
            String title,
            String address,
            String date,
            String time,
            boolean eventLocation) {
        this.title = title;
        this.address = address;
        this.date = date;
        this.time = time;
        this.eventLocation = eventLocation;
    }

    /**
     * Returns the point title.
     * @return point title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Returns the full address.
     * @return full address
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Returns the optional date.
     * @return date text
     */
    public String getDate() {
        return this.date;
    }

    /**
     * Returns the optional time.
     * @return time text
     */
    public String getTime() {
        return this.time;
    }

    /**
     * Returns whether this point is the event location.
     * @return true for the event location
     */
    public boolean isEventLocation() {
        return this.eventLocation;
    }
}

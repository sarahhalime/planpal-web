package entity;

public class Attendee {

    // attendeeId: Unique identifier for an attendee.
    // name: The name of the attendee.

    private final int attendeeId;
    private final String name;

    public Attendee(int attendeeId, String name) {

        this.attendeeId = attendeeId;
        this.name = name;

    }

    public int getAttendeeId() {
        return attendeeId;
    }

    public String getName() {
        return name;
    }

}

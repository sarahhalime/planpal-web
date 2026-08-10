package use_case.who_owes_what;

import java.util.List;

public class WhoOwesWhatOutputData {

    private final String eventName;
    private final String eventCurrency;
    private final List<AttendeeBalanceOutputData> attendeeBalances;

    public WhoOwesWhatOutputData(String eventName, List<AttendeeBalanceOutputData> attendeeBalances) {
        this(eventName, "CAD", attendeeBalances);
    }

    public WhoOwesWhatOutputData(String eventName, String eventCurrency,
                                 List<AttendeeBalanceOutputData> attendeeBalances) {

        this.eventName = eventName;
        this.eventCurrency = eventCurrency;
        this.attendeeBalances = attendeeBalances;

    }

    public String getEventName() {
        return eventName;
    }

    public String getEventCurrency() {
        return eventCurrency;
    }

    public List<AttendeeBalanceOutputData> getAttendeeBalances() {
        return attendeeBalances;
    }

}

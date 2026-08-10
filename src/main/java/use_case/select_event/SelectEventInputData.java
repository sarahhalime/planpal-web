package use_case.select_event;

/**
 * The input data for the select event use case.
 */
public class SelectEventInputData {

    private final String username;
    private final int newSelectedEventId;

    public SelectEventInputData(String username, int newSelectedEventId) {
        this.username = username;
        this.newSelectedEventId = newSelectedEventId;
    }

    public String getUsername() {
        return username;
    }

    public int getNewSelectedEventId() {
        return newSelectedEventId;
    }
}

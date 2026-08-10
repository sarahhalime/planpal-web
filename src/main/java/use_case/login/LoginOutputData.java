package use_case.login;

import java.util.List;

import entity.EventSummary;

public class LoginOutputData {
    private final String username;
    private final String displayName;
    private final String preferredCurrency;
    private final List<EventSummary> events;

    public LoginOutputData(String username, String displayName,
                           String preferredCurrency, List<EventSummary> events) {
        this.username = username;
        this.displayName = displayName;
        this.preferredCurrency = preferredCurrency;
        this.events = events;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPreferredCurrency() {
        return preferredCurrency;
    }

    public List<EventSummary> getEvents() {
        return events;
    }
}

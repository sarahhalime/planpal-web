package use_case.location_insight;

public class LocationInsightDataAccessException extends Exception {

    public LocationInsightDataAccessException(String message) {
        super(message);
    }

    public LocationInsightDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

}

package use_case.itinerary;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import entity.Activity;
import entity.Event;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/**
 * Builds a chronological itinerary from the activities of an event.
 */
public final class ItineraryInteractor implements ItineraryInputBoundary {
    private static final int TIGHT_BUFFER_MINUTES = 30;
    private static final int NO_TIME = ItineraryTravelEstimate.UNAVAILABLE_MINUTES;

    private final ItineraryDataAccessInterface dataAccessObject;
    private final ItineraryOutputBoundary presenter;
    private final ItineraryTravelGateway travelGateway;

    /**
     * Creates an itinerary interactor without travel routing.
     *
     * @param dataAccessObject event data access
     * @param presenter itinerary output boundary
     */
    public ItineraryInteractor(
            ItineraryDataAccessInterface dataAccessObject,
            ItineraryOutputBoundary presenter) {
        this(dataAccessObject, presenter, null);
    }

    /**
     * Creates an itinerary interactor.
     *
     * @param dataAccessObject event data access
     * @param presenter itinerary output boundary
     * @param travelGateway travel-time routing gateway
     */
    public ItineraryInteractor(
            ItineraryDataAccessInterface dataAccessObject,
            ItineraryOutputBoundary presenter,
            ItineraryTravelGateway travelGateway) {
        this.dataAccessObject = dataAccessObject;
        this.presenter = presenter;
        this.travelGateway = travelGateway;
    }

    @Override
    public void execute(ItineraryInputData inputData) {
        try {
            final Event event = this.dataAccessObject.getEvent(inputData.getEventId());
            final List<Activity> activities = new ArrayList<>(event.getActivityList());

            activities.sort(this.createActivityComparator());
            this.presenter.prepareSuccessView(new ItineraryOutputData(
                    this.buildItems(activities)
            ));
        }
        catch (WhoOwesWhatDataAccessException exception) {
            this.presenter.prepareFailView(exception.getMessage());
        }
    }

    private List<ItineraryItemOutputData> buildItems(List<Activity> activities) {
        final List<ItineraryItemOutputData> items = new ArrayList<>();

        for (int index = 0; index < activities.size(); index++) {
            final Activity activity = activities.get(index);
            ItineraryTravelOutputData travelToNext = null;

            if (index + 1 < activities.size()) {
                travelToNext = this.createTravelData(
                        activity,
                        activities.get(index + 1)
                );
            }
            items.add(new ItineraryItemOutputData(
                    activity.getActivityName(),
                    activity.getDate(),
                    activity.getTime(),
                    activity.getLocation(),
                    travelToNext
            ));
        }
        return items;
    }

    private ItineraryTravelOutputData createTravelData(Activity origin, Activity destination) {
        final String originLocation = this.safeText(origin.getLocation());
        final String destinationLocation = this.safeText(destination.getLocation());
        ItineraryTravelOutputData outputData = null;

        if (this.travelGateway != null
                && !originLocation.isBlank()
                && !destinationLocation.isBlank()) {
            try {
                final ItineraryTravelEstimate estimate =
                        this.travelGateway.estimateTravel(originLocation, destinationLocation);

                if (estimate.hasAnyTime()) {
                    outputData = this.compareSchedule(origin, destination, estimate);
                }
                else {
                    outputData = this.unavailableTravelData(ItineraryTravelFailure.NO_ROUTE);
                }
            }
            catch (ItineraryTravelException exception) {
                outputData = this.unavailableTravelData(exception.getFailure());
            }
        }
        return outputData;
    }

    private ItineraryTravelOutputData compareSchedule(
            Activity origin,
            Activity destination,
            ItineraryTravelEstimate estimate) {
        final int availableMinutes = this.availableMinutes(origin, destination);
        final int comparisonMinutes = this.comparisonMinutes(estimate);
        final int bufferMinutes;
        final ItineraryTravelStatus status;

        if (availableMinutes == NO_TIME || comparisonMinutes == NO_TIME) {
            bufferMinutes = NO_TIME;
            status = ItineraryTravelStatus.NO_SCHEDULE;
        }
        else {
            bufferMinutes = availableMinutes - comparisonMinutes;
            status = this.scheduleStatus(bufferMinutes);
        }

        return new ItineraryTravelOutputData(
                estimate.getDrivingMinutes(),
                estimate.getWalkingMinutes(),
                availableMinutes,
                bufferMinutes,
                status
        );
    }

    private ItineraryTravelOutputData unavailableTravelData(
            ItineraryTravelFailure failure) {
        return new ItineraryTravelOutputData(
                NO_TIME,
                NO_TIME,
                NO_TIME,
                NO_TIME,
                ItineraryTravelStatus.UNAVAILABLE,
                failure
        );
    }

    private int comparisonMinutes(ItineraryTravelEstimate estimate) {
        final int minutes;

        if (estimate.hasDrivingTime()) {
            minutes = estimate.getDrivingMinutes();
        }
        else if (estimate.hasWalkingTime()) {
            minutes = estimate.getWalkingMinutes();
        }
        else {
            minutes = NO_TIME;
        }
        return minutes;
    }

    private int availableMinutes(Activity origin, Activity destination) {
        final LocalDateTime originTime = this.activityDateTime(origin);
        final LocalDateTime destinationTime = this.activityDateTime(destination);
        final int minutes;

        if (originTime == null || destinationTime == null) {
            minutes = NO_TIME;
        }
        else {
            final long duration = Duration.between(originTime, destinationTime).toMinutes();

            if (duration > Integer.MAX_VALUE || duration < Integer.MIN_VALUE) {
                minutes = NO_TIME;
            }
            else {
                minutes = (int) duration;
            }
        }
        return minutes;
    }

    private LocalDateTime activityDateTime(Activity activity) {
        LocalDateTime dateTime = null;
        final String dateText = this.safeText(activity.getDate());
        final String timeText = this.safeText(activity.getTime());

        if (!dateText.isBlank() && !timeText.isBlank()) {
            try {
                dateTime = LocalDateTime.of(
                        LocalDate.parse(dateText),
                        LocalTime.parse(timeText)
                );
            }
            catch (DateTimeParseException exception) {
                dateTime = null;
            }
        }
        return dateTime;
    }

    private ItineraryTravelStatus scheduleStatus(int bufferMinutes) {
        final ItineraryTravelStatus status;

        if (bufferMinutes < 0) {
            status = ItineraryTravelStatus.INSUFFICIENT;
        }
        else if (bufferMinutes < TIGHT_BUFFER_MINUTES) {
            status = ItineraryTravelStatus.TIGHT;
        }
        else {
            status = ItineraryTravelStatus.COMFORTABLE;
        }
        return status;
    }

    private Comparator<Activity> createActivityComparator() {
        return Comparator
                .comparing(this::dateSortKey)
                .thenComparing(this::timeSortKey)
                .thenComparing(
                        activity -> this.safeText(activity.getActivityName()),
                        String.CASE_INSENSITIVE_ORDER
                );
    }

    private LocalDate dateSortKey(Activity activity) {
        final LocalDate date;
        final String dateText = this.safeText(activity.getDate());

        if (dateText.isBlank()) {
            date = LocalDate.MAX;
        }
        else {
            date = this.parseDate(dateText);
        }
        return date;
    }

    private LocalTime timeSortKey(Activity activity) {
        final LocalTime time;
        final String timeText = this.safeText(activity.getTime());

        if (timeText.isBlank()) {
            time = LocalTime.MAX;
        }
        else {
            time = this.parseTime(timeText);
        }
        return time;
    }

    private LocalDate parseDate(String dateText) {
        LocalDate date;

        try {
            date = LocalDate.parse(dateText);
        }
        catch (DateTimeParseException exception) {
            date = LocalDate.MAX.minusDays(1);
        }
        return date;
    }

    private LocalTime parseTime(String timeText) {
        LocalTime time;

        try {
            time = LocalTime.parse(timeText);
        }
        catch (DateTimeParseException exception) {
            time = LocalTime.MAX.minusNanos(1);
        }
        return time;
    }

    private String safeText(String text) {
        final String safeText;

        if (text == null) {
            safeText = "";
        }
        else {
            safeText = text;
        }
        return safeText;
    }
}

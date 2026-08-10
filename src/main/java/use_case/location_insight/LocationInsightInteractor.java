package use_case.location_insight;

import entity.LocationInsight;

/**
 * Retrieves and prepares location insight data.
 */
public final class LocationInsightInteractor implements LocationInsightInputBoundary {

    private static final String LOCATION_REQUIRED = "A location is required.";
    private static final String NO_INSIGHT = "No location insight was returned.";

    private final LocationInsightDataAccessInterface dataAccessInterface;
    private final LocationInsightOutputBoundary outputBoundary;

    /**
     * Creates the interactor.
     *
     * @param dataAccessInterface location-insight data access
     * @param outputBoundary output presenter
     */
    public LocationInsightInteractor(
            LocationInsightDataAccessInterface dataAccessInterface,
            LocationInsightOutputBoundary outputBoundary
    ) {
        this.dataAccessInterface = dataAccessInterface;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(LocationInsightInputData inputData) {
        if (inputData == null
                || inputData.getLocation() == null
                || inputData.getLocation().isBlank()) {
            this.outputBoundary.prepareFailView(LOCATION_REQUIRED);
        }
        else {
            this.loadInsight(inputData.getLocation().strip());
        }
    }

    private void loadInsight(String location) {
        try {
            final LocationInsight locationInsight =
                    this.dataAccessInterface.getLocationInsight(location);

            if (locationInsight == null) {
                this.outputBoundary.prepareFailView(NO_INSIGHT);
            }
            else {
                this.outputBoundary.prepareSuccessView(
                        this.createOutputData(locationInsight)
                );
            }
        }
        catch (LocationInsightDataAccessException exception) {
            this.outputBoundary.prepareFailView(exception.getMessage());
        }
    }

    private LocationInsightOutputData createOutputData(
            LocationInsight locationInsight
    ) {
        return new LocationInsightOutputData(
                locationInsight.getFunScore(),
                locationInsight.getSafetyScore(),
                locationInsight.getAccessibilityScore(),
                locationInsight.getAmenitiesScore(),
                locationInsight.getAffordabilityScore(),
                locationInsight.getTags()
        );
    }
}

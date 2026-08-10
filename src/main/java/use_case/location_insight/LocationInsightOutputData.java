package use_case.location_insight;

import java.util.List;

/**
 * Output data for location insight.
 */
public final class LocationInsightOutputData {

    private final int funScore;
    private final int safetyScore;
    private final int accessibilityScore;
    private final int amenitiesScore;
    private final int affordabilityScore;
    private final List<String> tagsList;

    /**
     * Creates output data.
     *
     * @param funScore fun-factor score
     * @param safetyScore safety score
     * @param accessibilityScore accessibility score
     * @param amenitiesScore nearby-amenities score
     * @param affordabilityScore affordability score
     * @param tagsList descriptive tags
     */
    public LocationInsightOutputData(
            int funScore,
            int safetyScore,
            int accessibilityScore,
            int amenitiesScore,
            int affordabilityScore,
            List<String> tagsList
    ) {
        this.funScore = funScore;
        this.safetyScore = safetyScore;
        this.accessibilityScore = accessibilityScore;
        this.amenitiesScore = amenitiesScore;
        this.affordabilityScore = affordabilityScore;
        this.tagsList = List.copyOf(tagsList);
    }

    public int getFunScore() {
        return this.funScore;
    }

    public int getSafetyScore() {
        return this.safetyScore;
    }

    public int getAccessibilityScore() {
        return this.accessibilityScore;
    }

    public int getAmenitiesScore() {
        return this.amenitiesScore;
    }

    public int getAffordabilityScore() {
        return this.affordabilityScore;
    }

    public List<String> getTagsList() {
        return this.tagsList;
    }
}

package entity;

import java.util.List;

/**
 * Stores AI-generated characteristics for an event location.
 */
public final class LocationInsight {

    private static final int MINIMUM_SCORE = 1;
    private static final int MAXIMUM_SCORE = 5;

    private String location;
    private int funScore;
    private int safetyScore;
    private int accessibilityScore;
    private int amenitiesScore;
    private int affordabilityScore;
    private List<String> tags;

    /**
     * Creates location insight data.
     *
     * @param location location name
     * @param funScore fun-factor score
     * @param safetyScore safety score
     * @param accessibilityScore accessibility score
     * @param amenitiesScore nearby-amenities score
     * @param affordabilityScore affordability score
     * @param tags short descriptive tags
     */
    public LocationInsight(
            String location,
            int funScore,
            int safetyScore,
            int accessibilityScore,
            int amenitiesScore,
            int affordabilityScore,
            List<String> tags
    ) {
        this.location = location;
        this.funScore = clampScore(funScore);
        this.safetyScore = clampScore(safetyScore);
        this.accessibilityScore = clampScore(accessibilityScore);
        this.amenitiesScore = clampScore(amenitiesScore);
        this.affordabilityScore = clampScore(affordabilityScore);
        this.tags = List.copyOf(tags);
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getFunScore() {
        return this.funScore;
    }

    public void setFunScore(int funScore) {
        this.funScore = clampScore(funScore);
    }

    public int getSafetyScore() {
        return this.safetyScore;
    }

    public void setSafetyScore(int safetyScore) {
        this.safetyScore = clampScore(safetyScore);
    }

    public int getAccessibilityScore() {
        return this.accessibilityScore;
    }

    public void setAccessibilityScore(int accessibilityScore) {
        this.accessibilityScore = clampScore(accessibilityScore);
    }

    public int getAmenitiesScore() {
        return this.amenitiesScore;
    }

    public void setAmenitiesScore(int amenitiesScore) {
        this.amenitiesScore = clampScore(amenitiesScore);
    }

    public int getAffordabilityScore() {
        return this.affordabilityScore;
    }

    public void setAffordabilityScore(int affordabilityScore) {
        this.affordabilityScore = clampScore(affordabilityScore);
    }

    public List<String> getTags() {
        return this.tags;
    }

    public void setTags(List<String> tags) {
        this.tags = List.copyOf(tags);
    }

    private static int clampScore(int score) {
        return Math.max(MINIMUM_SCORE, Math.min(MAXIMUM_SCORE, score));
    }
}

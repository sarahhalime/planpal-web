package use_case.location_insight;

import entity.LocationInsight;

public interface LocationInsightDataAccessInterface {

    /**
     * Gets a location insight object by a location.
     * @param location the location to get insight for
     * @return the location insight object
     * @throws LocationInsightDataAccessException if the location insight cannot be found
     */
    LocationInsight getLocationInsight(String location) throws LocationInsightDataAccessException;

}

package use_case.add_guests;

import java.util.List;

/**
 * The output boundary for the add guest use case.
 */
public interface AddGuestOutputBoundary {

    /**
     * This method prepares the success view for the Add Guest use case.
     * @param outputData the output data
     */
    void prepareSuccessView(AddGuestOutputData outputData);

    /**
     * This method prepares the failure view for the Add Guest use case.
     * @param errorMessage the error message of the failure
     */
    void prepareFailView(String errorMessage);

    /**
     * Sets the list of available usernames.
     * @param usernames the list of available usernames
     */
    void setAvaliableUsernames(List<String> usernames);
}

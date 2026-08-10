package use_case.follow_user;

/**
 * The output boundary for the follow user use case.
 */
public interface FollowUserOutputBoundary {

    /**
     * Prepares the success view for the follow user use case.
     * @param followUserOutputData the result of the follow request
     */
    void prepareSuccessView(FollowUserOutputData followUserOutputData);

    /**
     * Prepares the failure view for the follow user use case.
     * @param errorMessage the reason the request failed
     */
    void prepareFailView(String errorMessage);
}

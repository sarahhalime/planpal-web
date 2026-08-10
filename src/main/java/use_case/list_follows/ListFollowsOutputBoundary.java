package use_case.list_follows;

/**
 * The output boundary for the list follows use case.
 */
public interface ListFollowsOutputBoundary {

    /**
     * Presents the followers or following list.
     * @param listFollowsOutputData the list to present
     */
    void prepareSuccessView(ListFollowsOutputData listFollowsOutputData);
}

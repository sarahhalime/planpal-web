package use_case.list_follows;

/**
 * The input boundary for the list follows use case.
 */
public interface ListFollowsInputBoundary {

    /**
     * Lists the followers or following of a user.
     * @param listFollowsInputData the request
     */
    void execute(ListFollowsInputData listFollowsInputData);
}

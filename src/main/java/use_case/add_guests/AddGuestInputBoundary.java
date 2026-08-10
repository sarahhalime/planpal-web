package use_case.add_guests;

/**
 * The input boundary for actions which are related to adding a guest (attendee).
 */
public interface AddGuestInputBoundary {

    /**
     * This method executes the add guest use case.
     * @param addGuestInputData the input data
     */
    void execute(AddGuestInputData addGuestInputData);

    /**
     * Sets the list of usernames of the guests that are already registered.
     *
     * @param eventId the event to look up the guests for
     */
    void setAvaliableGuests(int eventId);
}

package use_case.add_guests;

import java.util.List;

/** Confirms a username belongs to a real registered user before adding them to an event. */
public interface AddGuestUserDataAccessInterface {
    /**
     * Checks if a username exists in the database.
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * This method retrieves all usernames from the database.
     * @return a list of all usernames
     */
    List<String> getAllUsernames();
}

package use_case.change_display_name;

import entity.User;

/**
 * The interface of the DAO for the Change Display Name Use Case.
 */
public interface ChangeDisplayNameDataAccessInterface {

    /**
     * Updates the system to record this user's display name.
     * @param user the user whose display name is to be updated
     */
    void changeDisplayName(User user);

    /**
     * Gets the Display name of a given username.
     * @param username the username of the user the display name belongs to
     * @return the display name of the user
     */
    String getDisplayName(String username);

    /**
     * Gets a user object by a user's username.
     * @param username the username of the user
     * @return the user object corresponding to the username
     */
    User getUser(String username);
}

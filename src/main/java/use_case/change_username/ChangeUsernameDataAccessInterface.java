package use_case.change_username;

import entity.User;

/**
 * The interface of the DAO for the Change username Use Case.
 */
public interface ChangeUsernameDataAccessInterface {
    /**
     * Updates the system to record this user's username.
     * @param oldUsername the old username of the user who wishes to change usernames
     * @param updatedUser the user whose username is to be updated
     */
    void changeUsername(String oldUsername, User updatedUser);

    /**
     * Gets if a username already exists for some user.
     * @param username the username to check for existance
     * @return true if username is already used, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Gets a user object by a user's username.
     * @param username the username of the user
     * @return the user object corresponding to the username
     */
    User getUser(String username);
}

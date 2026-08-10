package use_case.change_password;

import entity.User;

/**
 * The interface of the DAO for the Change password Use Case.
 */
public interface ChangePasswordDataAccessInterface {
    /**
     * Updates the system to record this user's password.
     * @param user the user whose password is to be updated
     */
    void changePassword(User user);

    /**
     * Gets the password of a given password.
     * @param username the username of the user the password belongs to
     * @return the password of the user
     */
    String getPassword(String username);

    /**
     * Gets the Display name of a given username.
     * @param username the username of the user the display name belongs to
     * @return the display name of the user
     */
    String getDisplayName(String username);

    /**
     * Returns the user associated with the given username.
     *
     * @param username the username of the user to retrieve
     * @return the user associated with the username
     */
    User getUser(String username);
}

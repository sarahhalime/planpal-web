package use_case.login;

import entity.User;

public interface LoginDataAccessInterface {

    /**
     * Checks if a user with the given username exists.
     * @param username the username to check
     * @return true if the user exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Gets a user object by a user's username.
     * @param username the username of the user
     * @return the user object corresponding to the username
     */
    User getUser(String username);

    /**
     * Finds the account registered to an email address, whatever it is called now.
     * Lets a signed-in-with-Google user keep their account after renaming themselves.
     *
     * @param email the email address to look up
     * @return the username of the matching account, or null when no account uses that email
     */
    default String findUsernameByEmail(String email) {
        return null;
    }

}

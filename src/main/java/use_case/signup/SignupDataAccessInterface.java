package use_case.signup;

import entity.User;

/**
 * The DAO for the signup use case.
 */
public interface SignupDataAccessInterface {

    /**
     * This method checks if the given username exists.
     * @param username the username to look for
     * @return true if a user with the given username exists otherwise it returns false
     */
    boolean existsByName(String username);

    /**
     * To save the user.
     * @param user the user to save
     */
    void save(User user);
}

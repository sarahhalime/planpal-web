package use_case.edit_home_address;

import entity.User;

public interface EditHomeAddressDataAccessInterface {

    /**
     * Gets a user object by a user's username.
     * @param username the username of the user
     * @return the user object corresponding to the username
     */
    User getUser(String username);

    /**
     * Changes the home address of a user.
     * @param user the user whose home address is to be changed
     */
    void changeHomeAddress(User user);
}

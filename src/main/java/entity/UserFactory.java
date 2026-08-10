package entity;

/**
 * Factory for creating users.
 */
public interface UserFactory {

    /**
     * Creates a new User without an email (email defaults to "").
     *
     * @param username the username of the new user, used for logging in
     * @param displayName the display name of the new user
     * @param password the password of the new user
     * @return the new user
     */
    User create(String username, String displayName, String password);

    /**
     * Creates a new User with an email.
     *
     * @param username the username of the new user, used for logging in
     * @param displayName the display name of the new user
     * @param email the email of the new user
     * @param password the password of the new user
     * @return the new user
     */
    User create(String username, String displayName, String email, String password);

    /**
     * Create a user with address.
     * @param username the username of the new user, used for Logging in
     * @param displayName the display name of the new user
     * @param email the email of the new user
     * @param password the password of the new user
     * @param address the address of the new user
     * @return the new user
     */
    User create(String username, String displayName, String email,
                String password, String address);

    /**
     * Creates a new user with an address and preferred currency.
     *
     * @param username the username of the new user, used for Logging in
     * @param displayName the display name of the new user
     * @param email the email of the new user
     * @param password the password of the new user
     * @param address the address of the new user
     * @param preferredCurrency the preferred currency code
     * @return the new user
     */
    User create(String username, String displayName, String email,
                String password, String address, String preferredCurrency);
}

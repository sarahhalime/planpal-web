package entity;

/**
 * A user in our program.
 */
public interface User {

    /**
     * Returns the username used to log in.
     * @return the username of the user.
     */
    String getUsername();

    /**
     * Returns the display name shown in the app.
     * @return the display name of the user.
     */
    String getDisplayName();

    /**
     * Returns the username of the user. Kept for code that calls getName().
     * @return the username of the user.
     */
    String getName();

    /**
     * Returns the email of the user.
     * @return the email of the user.
     */
    String getEmail();

    /**
     * Returns the password of the user.
     * @return the password of the user.
     */
    String getPassword();

    /**
     * Returns the user's preferred currency.
     *
     * @return the preferred currency code
     */
    String getPreferredCurrency();

}

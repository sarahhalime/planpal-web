package use_case.change_preferred_currency;

/**
 * User data-access operations required by the change-preferred-currency use case.
 */
public interface ChangePreferredCurrencyUserDataAccessInterface {

    /**
     * Returns whether a user exists with the given username.
     *
     * @param username the username to search for
     * @return whether the user exists
     */
    boolean existsByUsername(String username);

    /**
     * Changes the user's preferred currency.
     *
     * @param username the username of the user to update
     * @param preferredCurrencyCode the new preferred currency code
     */
    void changePreferredCurrency(String username, String preferredCurrencyCode);
}

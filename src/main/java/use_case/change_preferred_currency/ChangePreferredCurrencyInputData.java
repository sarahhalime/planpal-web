package use_case.change_preferred_currency;

/**
 * The input data for the change-preferred-currency use case.
 */
public class ChangePreferredCurrencyInputData {

    private final String username;
    private final String preferredCurrencyCode;

    /**
     * Creates the input data for changing a user's preferred currency.
     *
     * @param username the username of the user
     * @param preferredCurrencyCode the new preferred currency code
     */
    public ChangePreferredCurrencyInputData(String username, String preferredCurrencyCode) {
        this.username = username;
        this.preferredCurrencyCode = preferredCurrencyCode;
    }

    /**
     * Returns the username.
     *
     * @return the username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Returns the preferred currency code.
     *
     * @return the preferred currency code
     */
    public String getPreferredCurrencyCode() {
        return this.preferredCurrencyCode;
    }
}

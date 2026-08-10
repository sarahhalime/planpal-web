package use_case.change_preferred_currency;

/**
 * The output data for the change-preferred-currency use case.
 */
public class ChangePreferredCurrencyOutputData {

    private final String username;
    private final String preferredCurrencyCode;

    /**
     * Creates the output data for a changed preferred currency.
     *
     * @param username the username of the updated user
     * @param preferredCurrencyCode the user's new preferred currency code
     */
    public ChangePreferredCurrencyOutputData(String username, String preferredCurrencyCode) {
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
     * Returns the user's preferred currency code.
     *
     * @return the preferred currency code
     */
    public String getPreferredCurrencyCode() {
        return this.preferredCurrencyCode;
    }
}

package use_case.change_preferred_currency;

/**
 * Currency data-access operations required by the change-preferred-currency use case.
 */
public interface ChangePreferredCurrencyCurrencyDataAccessInterface {

    /**
     * Returns whether the currency service supports the given currency code.
     *
     * @param currencyCode the currency code to check
     * @return whether the currency is supported
     */
    boolean isSupportedCurrency(String currencyCode);
}

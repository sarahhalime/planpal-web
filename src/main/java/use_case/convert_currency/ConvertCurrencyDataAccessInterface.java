package use_case.convert_currency;

/**
 * The data access interface for the convert-currency use case.
 */
public interface ConvertCurrencyDataAccessInterface {

    /**
     * Returns the exchange rate between two currencies.
     *
     * @param sourceCurrencyCode the source currency code
     * @param targetCurrencyCode the target currency code
     * @return the exchange-rate data
     */
    ExchangeRateData getExchangeRate(String sourceCurrencyCode,
                                     String targetCurrencyCode);
}

package use_case.get_supported_currencies;

import java.util.List;

/**
 * The data access interface for the get-supported-currencies use case.
 */
public interface GetSupportedCurrenciesDataAccessInterface {

    /**
     * Returns all currencies supported by the currency service.
     *
     * @return the supported currencies
     */
    List<CurrencyOptionData> getSupportedCurrencies();
}

package use_case.get_supported_currencies;

import java.util.List;

/**
 * The output data for the get-supported-currencies use case.
 */
public class GetSupportedCurrenciesOutputData {

    private final List<CurrencyOptionData> currencies;

    /**
     * Creates output data containing the supported currencies.
     *
     * @param currencies the supported currencies
     */
    public GetSupportedCurrenciesOutputData(
            List<CurrencyOptionData> currencies) {
        this.currencies = List.copyOf(currencies);
    }

    /**
     * Returns the supported currencies.
     *
     * @return the supported currencies
     */
    public List<CurrencyOptionData> getCurrencies() {
        return this.currencies;
    }
}

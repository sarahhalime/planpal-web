package use_case.get_supported_currencies;

/**
 * The input boundary for the get-supported-currencies use case.
 */
public interface GetSupportedCurrenciesInputBoundary {

    /**
     * Retrieves the currencies supported by the currency service.
     */
    void execute();
}

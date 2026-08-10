package use_case.convert_currency;

/**
 * The input boundary for the convert-currency use case.
 */
public interface ConvertCurrencyInputBoundary {

    /**
     * Converts an amount from its source currency into a target currency.
     *
     * @param inputData the conversion input data
     */
    void execute(ConvertCurrencyInputData inputData);
}

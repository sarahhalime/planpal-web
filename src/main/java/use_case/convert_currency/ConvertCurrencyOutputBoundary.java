package use_case.convert_currency;

/**
 * The output boundary for the convert-currency use case.
 */
public interface ConvertCurrencyOutputBoundary {

    /**
     * Prepares the success view for the convert-currency use case.
     *
     * @param outputData the output data
     */
    void prepareSuccessView(ConvertCurrencyOutputData outputData);

    /**
     * Prepares the failure view for the convert-currency use case.
     *
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}

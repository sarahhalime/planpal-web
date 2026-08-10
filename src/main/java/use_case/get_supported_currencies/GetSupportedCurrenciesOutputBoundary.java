package use_case.get_supported_currencies;

/**
 * The output boundary for the get-supported-currencies use case.
 */
public interface GetSupportedCurrenciesOutputBoundary {

    /**
     * Prepares the success view for the get-supported-currencies use case.
     *
     * @param outputData the output data
     */
    void prepareSuccessView(GetSupportedCurrenciesOutputData outputData);

    /**
     * Prepares the failure view for the get-supported-currencies use case.
     *
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}

package use_case.change_preferred_currency;

/**
 * The output boundary for the change-preferred-currency use case.
 */
public interface ChangePreferredCurrencyOutputBoundary {

    /**
     * Prepares the success view for the change-preferred-currency use case.
     *
     * @param outputData the output data
     */
    void prepareSuccessView(ChangePreferredCurrencyOutputData outputData);

    /**
     * Prepares the failure view for the change-preferred-currency use case.
     *
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}

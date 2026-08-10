package use_case.change_preferred_currency;

/**
 * The input boundary for the change-preferred-currency use case.
 */
public interface ChangePreferredCurrencyInputBoundary {

    /**
     * Changes a user's preferred currency.
     *
     * @param inputData the information needed to change the preferred currency
     */
    void execute(ChangePreferredCurrencyInputData inputData);
}

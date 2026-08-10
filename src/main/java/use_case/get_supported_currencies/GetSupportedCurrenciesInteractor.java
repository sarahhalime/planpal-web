package use_case.get_supported_currencies;

import java.util.List;

/**
 * The interactor for the get-supported-currencies use case.
 */
public class GetSupportedCurrenciesInteractor
        implements GetSupportedCurrenciesInputBoundary {

    private static final String FAILURE_MESSAGE =
            "Unable to load supported currencies.";

    private final GetSupportedCurrenciesDataAccessInterface dataAccessObject;
    private final GetSupportedCurrenciesOutputBoundary presenter;

    /**
     * Creates a get-supported-currencies interactor.
     *
     * @param dataAccessObject the currency data access object
     * @param presenter the output boundary
     */
    public GetSupportedCurrenciesInteractor(
            GetSupportedCurrenciesDataAccessInterface dataAccessObject,
            GetSupportedCurrenciesOutputBoundary presenter) {
        this.dataAccessObject = dataAccessObject;
        this.presenter = presenter;
    }

    @Override
    public void execute() {
        try {
            final List<CurrencyOptionData> currencies =
                    this.dataAccessObject.getSupportedCurrencies();

            if (currencies == null || currencies.isEmpty()) {
                this.presenter.prepareFailView(FAILURE_MESSAGE);
            }
            else {
                final GetSupportedCurrenciesOutputData outputData =
                        new GetSupportedCurrenciesOutputData(currencies);
                this.presenter.prepareSuccessView(outputData);
            }
        }
        catch (IllegalStateException | IllegalArgumentException exception) {
            this.presenter.prepareFailView(FAILURE_MESSAGE);
        }
    }
}

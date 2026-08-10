package use_case.change_preferred_currency;

import java.util.Locale;

/**
 * The interactor for the change-preferred-currency use case.
 */
public class ChangePreferredCurrencyInteractor
        implements ChangePreferredCurrencyInputBoundary {

    private static final String MISSING_INFORMATION_MESSAGE =
            "Preferred currency information is missing.";
    private static final String USER_NOT_FOUND_MESSAGE =
            "The user could not be found.";
    private static final String UNSUPPORTED_CURRENCY_MESSAGE =
            "The selected currency is not supported.";
    private static final String FAILURE_MESSAGE =
            "Unable to change preferred currency.";

    private final ChangePreferredCurrencyUserDataAccessInterface userDataAccessObject;
    private final ChangePreferredCurrencyCurrencyDataAccessInterface currencyDataAccessObject;
    private final ChangePreferredCurrencyOutputBoundary presenter;

    /**
     * Creates a change-preferred-currency interactor.
     *
     * @param userDataAccessObject the user data access object
     * @param currencyDataAccessObject the currency data access object
     * @param presenter the output boundary
     */
    public ChangePreferredCurrencyInteractor(
            ChangePreferredCurrencyUserDataAccessInterface userDataAccessObject,
            ChangePreferredCurrencyCurrencyDataAccessInterface currencyDataAccessObject,
            ChangePreferredCurrencyOutputBoundary presenter) {
        this.userDataAccessObject = userDataAccessObject;
        this.currencyDataAccessObject = currencyDataAccessObject;
        this.presenter = presenter;
    }

    @Override
    public void execute(ChangePreferredCurrencyInputData inputData) {
        if (this.hasMissingInformation(inputData)) {
            this.presenter.prepareFailView(MISSING_INFORMATION_MESSAGE);
        }
        else {
            this.changeCurrency(inputData);
        }
    }

    private void changeCurrency(ChangePreferredCurrencyInputData inputData) {

        final String username = inputData.getUsername().trim();
        final String currencyCode = inputData.getPreferredCurrencyCode()
                .trim()
                .toUpperCase(Locale.ROOT);

        try {
            if (!this.userDataAccessObject.existsByUsername(username)) {
                this.presenter.prepareFailView(USER_NOT_FOUND_MESSAGE);
            }
            else if (!this.currencyDataAccessObject.isSupportedCurrency(currencyCode)) {
                this.presenter.prepareFailView(UNSUPPORTED_CURRENCY_MESSAGE);
            }
            else {
                this.userDataAccessObject.changePreferredCurrency(
                        username,
                        currencyCode
                );
                final ChangePreferredCurrencyOutputData outputData =
                        new ChangePreferredCurrencyOutputData(
                                username,
                                currencyCode
                        );
                this.presenter.prepareSuccessView(outputData);
            }
        }
        catch (IllegalStateException | IllegalArgumentException exception) {
            this.presenter.prepareFailView(FAILURE_MESSAGE);
        }
    }

    private boolean hasMissingInformation(
            ChangePreferredCurrencyInputData inputData) {
        return inputData == null
                || inputData.getUsername() == null
                || inputData.getUsername().isBlank()
                || inputData.getPreferredCurrencyCode() == null
                || inputData.getPreferredCurrencyCode().isBlank();
    }
}

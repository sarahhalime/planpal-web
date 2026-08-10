package use_case.convert_currency;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * The interactor for the convert-currency use case.
 */
public class ConvertCurrencyInteractor implements ConvertCurrencyInputBoundary {

    private static final String MISSING_INFORMATION_MESSAGE =
            "Currency conversion information is missing.";
    private static final String INVALID_AMOUNT_MESSAGE =
            "The amount cannot be negative.";
    private static final String INVALID_CURRENCY_CODE_MESSAGE =
            "Currency codes must contain exactly three letters.";
    private static final String FAILURE_MESSAGE =
            "Unable to convert the currency.";

    private final ConvertCurrencyDataAccessInterface dataAccessObject;
    private final ConvertCurrencyOutputBoundary presenter;

    /**
     * Creates a convert-currency interactor.
     *
     * @param dataAccessObject the currency data access object
     * @param presenter the output boundary
     */
    public ConvertCurrencyInteractor(
            ConvertCurrencyDataAccessInterface dataAccessObject,
            ConvertCurrencyOutputBoundary presenter) {
        this.dataAccessObject = dataAccessObject;
        this.presenter = presenter;
    }

    @Override
    public void execute(ConvertCurrencyInputData inputData) {
        if (hasMissingInformation(inputData)) {
            this.presenter.prepareFailView(MISSING_INFORMATION_MESSAGE);
        }
        else if (inputData.getAmount().signum() < 0) {
            this.presenter.prepareFailView(INVALID_AMOUNT_MESSAGE);
        }
        else if (!hasValidCurrencyCodes(inputData)) {
            this.presenter.prepareFailView(INVALID_CURRENCY_CODE_MESSAGE);
        }
        else {
            convert(inputData);
        }
    }

    private void convert(ConvertCurrencyInputData inputData) {
        final String sourceCurrencyCode = normalizeCurrencyCode(
                inputData.getSourceCurrencyCode());
        final String targetCurrencyCode = normalizeCurrencyCode(
                inputData.getTargetCurrencyCode());

        if (sourceCurrencyCode.equals(targetCurrencyCode)) {
            prepareSuccessView(inputData.getAmount(), sourceCurrencyCode,
                    targetCurrencyCode, BigDecimal.ONE);
        }
        else {
            try {
                final ExchangeRateData exchangeRateData =
                        this.dataAccessObject.getExchangeRate(
                                sourceCurrencyCode, targetCurrencyCode);

                if (hasValidExchangeRate(exchangeRateData,
                        sourceCurrencyCode, targetCurrencyCode)) {
                    prepareSuccessView(inputData.getAmount(),
                            sourceCurrencyCode, targetCurrencyCode,
                            exchangeRateData.getRate());
                }
                else {
                    this.presenter.prepareFailView(FAILURE_MESSAGE);
                }
            }
            catch (IllegalStateException | IllegalArgumentException exception) {
                this.presenter.prepareFailView(FAILURE_MESSAGE);
            }
        }
    }

    private void prepareSuccessView(BigDecimal originalAmount, String sourceCurrencyCode,
                                    String targetCurrencyCode, BigDecimal exchangeRate) {
        final BigDecimal convertedAmount = originalAmount.multiply(exchangeRate);
        final ConvertCurrencyOutputData outputData =
                new ConvertCurrencyOutputData(originalAmount, sourceCurrencyCode,
                        convertedAmount, targetCurrencyCode, exchangeRate);
        this.presenter.prepareSuccessView(outputData);
    }

    private static boolean hasMissingInformation(
            ConvertCurrencyInputData inputData) {
        return inputData == null
                || inputData.getAmount() == null
                || inputData.getSourceCurrencyCode() == null
                || inputData.getTargetCurrencyCode() == null
                || inputData.getSourceCurrencyCode().isBlank()
                || inputData.getTargetCurrencyCode().isBlank();
    }

    private static boolean hasValidCurrencyCodes(
            ConvertCurrencyInputData inputData) {
        return inputData.getSourceCurrencyCode().trim().matches("[A-Za-z]{3}")
                && inputData.getTargetCurrencyCode().trim()
                .matches("[A-Za-z]{3}");
    }

    private static String normalizeCurrencyCode(String currencyCode) {
        return currencyCode.trim().toUpperCase(Locale.ROOT);
    }

    private static boolean hasValidExchangeRate(
            ExchangeRateData exchangeRateData,
            String sourceCurrencyCode,
            String targetCurrencyCode) {
        return exchangeRateData != null
                && exchangeRateData.getRate() != null
                && exchangeRateData.getRate().signum() > 0
                && sourceCurrencyCode.equalsIgnoreCase(
                        exchangeRateData.getSourceCurrencyCode())
                && targetCurrencyCode.equalsIgnoreCase(
                        exchangeRateData.getTargetCurrencyCode());
    }
}

package use_case.convert_currency;

import java.math.BigDecimal;

/**
 * The output data for the convert-currency use case.
 */
public class ConvertCurrencyOutputData {

    private final BigDecimal originalAmount;
    private final String sourceCurrencyCode;
    private final BigDecimal convertedAmount;
    private final String targetCurrencyCode;
    private final BigDecimal exchangeRate;

    /**
     * Creates output data for a completed currency conversion.
     *
     * @param originalAmount the amount before conversion
     * @param sourceCurrencyCode the currency code of the original amount
     * @param convertedAmount the amount after conversion
     * @param targetCurrencyCode the currency code of the converted amount
     * @param exchangeRate the exchange rate used for the conversion
     */
    public ConvertCurrencyOutputData(BigDecimal originalAmount, String sourceCurrencyCode,
                                     BigDecimal convertedAmount, String targetCurrencyCode,
                                     BigDecimal exchangeRate) {
        this.originalAmount = originalAmount;
        this.sourceCurrencyCode = sourceCurrencyCode;
        this.convertedAmount = convertedAmount;
        this.targetCurrencyCode = targetCurrencyCode;
        this.exchangeRate = exchangeRate;
    }

    /**
     * Returns the original amount.
     *
     * @return the original amount
     */
    public BigDecimal getOriginalAmount() {
        return this.originalAmount;
    }

    /**
     * Returns the source currency code.
     *
     * @return the source currency code
     */
    public String getSourceCurrencyCode() {
        return this.sourceCurrencyCode;
    }

    /**
     * Returns the converted amount.
     *
     * @return the converted amount
     */
    public BigDecimal getConvertedAmount() {
        return this.convertedAmount;
    }

    /**
     * Returns the target currency code.
     *
     * @return the target currency code
     */
    public String getTargetCurrencyCode() {
        return this.targetCurrencyCode;
    }

    /**
     * Returns the exchange rate used for the conversion.
     *
     * @return the exchange rate
     */
    public BigDecimal getExchangeRate() {
        return this.exchangeRate;
    }
}

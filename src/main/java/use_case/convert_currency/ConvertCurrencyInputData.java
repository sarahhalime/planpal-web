package use_case.convert_currency;

import java.math.BigDecimal;

/**
 * The input data for the convert-currency use case.
 */
public class ConvertCurrencyInputData {

    private final BigDecimal amount;
    private final String sourceCurrencyCode;
    private final String targetCurrencyCode;

    /**
     * Creates the input data for a currency conversion.
     *
     * @param amount the original amount
     * @param sourceCurrencyCode the currency code of the original amount
     * @param targetCurrencyCode the currency code to convert the amount into
     */
    public ConvertCurrencyInputData(BigDecimal amount, String sourceCurrencyCode,
                                    String targetCurrencyCode) {
        this.amount = amount;
        this.sourceCurrencyCode = sourceCurrencyCode;
        this.targetCurrencyCode = targetCurrencyCode;
    }

    /**
     * Returns the original amount.
     *
     * @return the original amount
     */
    public BigDecimal getAmount() {
        return this.amount;
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
     * Returns the target currency code.
     *
     * @return the target currency code
     */
    public String getTargetCurrencyCode() {
        return this.targetCurrencyCode;
    }
}

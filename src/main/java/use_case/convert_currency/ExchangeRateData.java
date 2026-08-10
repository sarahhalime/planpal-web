package use_case.convert_currency;

import java.math.BigDecimal;

/**
 * Contains an exchange rate returned by a currency data source.
 */
public class ExchangeRateData {

    private final String sourceCurrencyCode;
    private final String targetCurrencyCode;
    private final BigDecimal rate;

    /**
     * Creates exchange-rate data.
     *
     * @param sourceCurrencyCode the source currency code
     * @param targetCurrencyCode the target currency code
     * @param rate the number of target-currency units per source-currency unit
     */
    public ExchangeRateData(String sourceCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        this.sourceCurrencyCode = sourceCurrencyCode;
        this.targetCurrencyCode = targetCurrencyCode;
        this.rate = rate;
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

    /**
     * Returns the exchange rate.
     *
     * @return the number of target-currency units per source-currency unit
     */
    public BigDecimal getRate() {
        return this.rate;
    }
}

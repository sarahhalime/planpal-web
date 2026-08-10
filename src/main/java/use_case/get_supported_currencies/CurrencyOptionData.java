package use_case.get_supported_currencies;

/**
 * Contains information about a supported currency.
 */
public class CurrencyOptionData {

    private final String code;
    private final String name;
    private final String symbol;

    /**
     * Creates a supported currency option.
     *
     * @param code the ISO 4217 currency code
     * @param name the full currency name
     * @param symbol the currency symbol, or null if unavailable
     */
    public CurrencyOptionData(String code, String name, String symbol) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
    }

    /**
     * Returns the ISO 4217 currency code.
     *
     * @return the currency code
     */
    public String getCode() {
        return this.code;
    }

    /**
     * Returns the full currency name.
     *
     * @return the currency name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the currency symbol.
     *
     * @return the currency symbol, or null if unavailable
     */
    public String getSymbol() {
        return this.symbol;
    }

    @Override
    public String toString() {
        return this.code;
    }
}

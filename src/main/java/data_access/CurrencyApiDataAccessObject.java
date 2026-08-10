package data_access;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import use_case.change_preferred_currency.ChangePreferredCurrencyCurrencyDataAccessInterface;
import use_case.convert_currency.ConvertCurrencyDataAccessInterface;
import use_case.convert_currency.ExchangeRateData;
import use_case.get_supported_currencies.CurrencyOptionData;
import use_case.get_supported_currencies.GetSupportedCurrenciesDataAccessInterface;

/**
 * Retrieves currency information from the Frankfurter API.
 */
public final class CurrencyApiDataAccessObject
        implements GetSupportedCurrenciesDataAccessInterface,
        ConvertCurrencyDataAccessInterface,
        ChangePreferredCurrencyCurrencyDataAccessInterface {

    private static final int HTTP_OK = 200;
    private static final String BASE_URL = "https://api.frankfurter.dev/v2";

    private final HttpClient httpClient;

    /**
     * Creates a new currency API data access object.
     */
    public CurrencyApiDataAccessObject() {
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public List<CurrencyOptionData> getSupportedCurrencies() {
        try {
            final String responseBody = this.sendGetRequest("/currencies");
            final JSONArray currenciesJson = new JSONArray(responseBody);
            final List<CurrencyOptionData> currencies = new ArrayList<>();

            for (int index = 0; index < currenciesJson.length(); index++) {
                final JSONObject currencyJson = currenciesJson.getJSONObject(index);
                final String code = currencyJson.getString("iso_code");
                final String name = currencyJson.getString("name");

                String symbol = null;
                if (!currencyJson.isNull("symbol")) {
                    symbol = currencyJson.getString("symbol");
                }

                currencies.add(new CurrencyOptionData(code, name, symbol));
            }

            return currencies;
        }
        catch (JSONException exception) {
            throw new IllegalStateException(
                    "The currency service returned invalid data.",
                    exception
            );
        }
    }

    @Override
    public ExchangeRateData getExchangeRate(String sourceCurrencyCode,
                                            String targetCurrencyCode) {
        final String sourceCode = this.formatCurrencyCode(sourceCurrencyCode);
        final String targetCode = this.formatCurrencyCode(targetCurrencyCode);

        final ExchangeRateData result;
        if (sourceCode.equals(targetCode)) {
            result = new ExchangeRateData(sourceCode, targetCode, BigDecimal.ONE);
        }
        else {
            result = this.fetchExchangeRate(sourceCode, targetCode);
        }
        return result;
    }

    private ExchangeRateData fetchExchangeRate(String sourceCode, String targetCode) {
        try {
            final String path = "/rate/" + sourceCode + "/" + targetCode;
            final JSONObject rateJson = new JSONObject(this.sendGetRequest(path));
            final BigDecimal rate = new BigDecimal(rateJson.get("rate").toString());

            return new ExchangeRateData(sourceCode, targetCode, rate);
        }
        catch (JSONException | NumberFormatException exception) {
            throw new IllegalStateException(
                    "The currency service returned invalid data.",
                    exception
            );
        }
    }

    @Override
    public boolean isSupportedCurrency(String currencyCode) {
        String formattedCode = null;

        try {
            formattedCode = this.formatCurrencyCode(currencyCode);
        }
        catch (IllegalArgumentException exception) {
            formattedCode = null;
        }

        boolean supported = false;
        if (formattedCode != null) {
            for (final CurrencyOptionData currency : this.getSupportedCurrencies()) {
                if (currency.getCode().equals(formattedCode)) {
                    supported = true;
                    break;
                }
            }
        }
        return supported;
    }

    private String sendGetRequest(String path) {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .build();

        try {
            final HttpResponse<String> response = this.httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != HTTP_OK) {
                throw new IllegalStateException("The currency service request failed.");
            }

            return response.body();
        }
        catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not connect to the currency service.",
                    exception
            );
        }
        catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("The currency request was interrupted.", exception);
        }
    }

    private String formatCurrencyCode(String currencyCode) {
        if (currencyCode == null) {
            throw new IllegalArgumentException("Currency code cannot be null.");
        }

        final String formattedCode = currencyCode.trim().toUpperCase(Locale.ROOT);
        if (!formattedCode.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Currency code must contain three letters.");
        }

        return formattedCode;
    }
}

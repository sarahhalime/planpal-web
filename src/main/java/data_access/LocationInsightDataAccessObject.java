package data_access;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.genai.Client;
import com.google.genai.errors.ApiException;
import com.google.genai.errors.GenAiIOException;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import entity.LocationInsight;
import use_case.location_insight.LocationInsightDataAccessException;
import use_case.location_insight.LocationInsightDataAccessInterface;

/**
 * Retrieves AI-generated location insight from Gemini.
 */
public final class LocationInsightDataAccessObject
        implements LocationInsightDataAccessInterface {

    private static final String TYPE = "type";
    private static final String PROPERTIES = "properties";
    private static final String REQUIRED = "required";
    private static final String ADDITIONAL_PROPERTIES = "additionalProperties";
    private static final String FUN_SCORE = "funScore";
    private static final String SAFETY_SCORE = "safetyScore";
    private static final String ACCESSIBILITY_SCORE = "accessibilityScore";
    private static final String AMENITIES_SCORE = "amenitiesScore";
    private static final String AFFORDABILITY_SCORE = "affordabilityScore";
    private static final String TAGS = "tags";
    private static final int TAG_COUNT = 3;
    private static final int MAXIMUM_SCORE = 5;
    private static final int MINIMUM_SCORE = 1;
    private static final String MODEL_NAME = "gemini-3.1-flash-lite";

    private final Client client = createClient();

    /**
     * Builds the Gemini client, handing it the api key when one is configured.
     *
     * <p>The Gemini sdk only reads {@code GOOGLE_API_KEY}/{@code GEMINI_API_KEY} from the real
     * process environment, so a key that lives in the {@code .env} file has to be passed in
     * here. When no key is configured anywhere the sdk's own lookup is used, so the error
     * message stays the same as before.
     *
     * @return the Gemini client
     */
    private static Client createClient() {
        String apiKey = EnvConfig.get("GOOGLE_API_KEY", "google.api.key");
        if (apiKey == null) {
            apiKey = EnvConfig.get("GEMINI_API_KEY", "gemini.api.key");
        }

        final Client result;
        if (apiKey == null) {
            result = new Client();
        }
        else {
            result = Client.builder().apiKey(apiKey).build();
        }
        return result;
    }

    @Override
    public LocationInsight getLocationInsight(String location)
            throws LocationInsightDataAccessException {

        final LocationInsight insight;

        if (location == null || location.isBlank()) {
            throw new LocationInsightDataAccessException(
                    "A location is required."
            );
        }

        try {
            insight = this.requestInsight(location.strip());
        }
        catch (JSONException | IllegalArgumentException exception) {
            throw new LocationInsightDataAccessException(
                    "Gemini returned invalid location insight data."
            );
        }
        catch (ApiException exception) {
            throw new LocationInsightDataAccessException(
                    "Gemini returned an error: " + exception.message()
            );
        }
        catch (GenAiIOException exception) {
            throw new LocationInsightDataAccessException(
                    "Could not connect to the location insight service."
            );
        }

        return insight;
    }

    private LocationInsight requestInsight(String location)
            throws LocationInsightDataAccessException {

        final GenerateContentConfig config =
                GenerateContentConfig.builder()
                        .responseMimeType("application/json")
                        .responseJsonSchema(this.createSchema())
                        .candidateCount(MINIMUM_SCORE)
                        .build();
        final GenerateContentResponse response =
                this.client.models.generateContent(
                        MODEL_NAME,
                        this.createPrompt(location),
                        config
                );
        final String responseText = response.text();

        if (responseText == null || responseText.isBlank()) {
            throw new LocationInsightDataAccessException(
                    "Gemini returned an empty response."
            );
        }

        return this.parseInsight(location, new JSONObject(responseText));
    }

    private Map<String, Object> createSchema() {
        final Map<String, Object> scoreSchema = Map.of(
                TYPE, "integer",
                "minimum", MINIMUM_SCORE,
                "maximum", MAXIMUM_SCORE
        );
        final Map<String, Object> tagSchema = this.createStringArraySchema(
                TAG_COUNT,
                TAG_COUNT
        );
        return Map.of(
                TYPE, "object",
                PROPERTIES, Map.of(
                        FUN_SCORE, scoreSchema,
                        SAFETY_SCORE, scoreSchema,
                        ACCESSIBILITY_SCORE, scoreSchema,
                        AMENITIES_SCORE, scoreSchema,
                        AFFORDABILITY_SCORE, scoreSchema,
                        TAGS, tagSchema
                ),
                REQUIRED, List.of(
                        FUN_SCORE,
                        SAFETY_SCORE,
                        ACCESSIBILITY_SCORE,
                        AMENITIES_SCORE,
                        AFFORDABILITY_SCORE,
                        TAGS
                ),
                ADDITIONAL_PROPERTIES, false
        );
    }

    private Map<String, Object> createStringArraySchema(
            int minimumItems,
            int maximumItems
    ) {
        return Map.of(
                TYPE, "array",
                "minItems", minimumItems,
                "maxItems", maximumItems,
                "items", Map.of(TYPE, "string")
        );
    }

    private String createPrompt(String location) {
        return String.format(
                "Evaluate this location for a general social event.%n%n"
                        + "Location: %s%n%n"
                        + "Return five independent scores from 1 to 5:%n"
                        + "- funScore: entertainment, attractions, food, atmosphere, "
                        + "uniqueness, and things to do. Do not consider safety.%n"
                        + "- safetyScore: general visitor safety only. Do not consider "
                        + "entertainment value. Do not invent crime statistics.%n"
                        + "- accessibilityScore: transit, walkability, parking, and "
                        + "mobility accessibility.%n"
                        + "- amenitiesScore: availability of nearby food, hotels, washrooms, "
                        + "shops, and useful visitor services. Do not score transit here.%n"
                        + "- affordabilityScore: how affordable food, attractions, lodging, "
                        + "and local activities are for a typical visitor.%n%n"
                        + "Also return exactly three short descriptive tags.%n"
                        + "Keep the tags concise and useful for event planning.",
                location
        );
    }

    private LocationInsight parseInsight(
            String location,
            JSONObject json
    ) {
        return new LocationInsight(
                location,
                json.getInt(FUN_SCORE),
                json.getInt(SAFETY_SCORE),
                json.getInt(ACCESSIBILITY_SCORE),
                json.getInt(AMENITIES_SCORE),
                json.getInt(AFFORDABILITY_SCORE),
                this.readStringList(json.getJSONArray(TAGS))
        );
    }

    private List<String> readStringList(JSONArray array) {
        final List<String> values = new ArrayList<>();

        for (int index = 0; index < array.length(); index++) {
            values.add(array.getString(index));
        }

        return values;
    }
}

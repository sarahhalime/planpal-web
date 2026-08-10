package data_access;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads configuration values such as API keys and OAuth credentials.
 *
 * <p>A value is looked up in this order, and the first one found wins:
 * <ol>
 *     <li>A real environment variable, e.g. {@code GOOGLE_MAPS_API_KEY=... java -jar PlanPal.jar}.</li>
 *     <li>A JVM system property, settable per-run with {@code -Dgoogle.maps.api.key=...}.</li>
 *     <li>An entry in the {@code .env} file beside the application.</li>
 * </ol>
 *
 * <p>The {@code .env} file holds one {@code NAME=value} pair per line. Blank lines and lines
 * starting with {@code #} are ignored, as are surrounding quotes around a value. This file
 * holds live credentials and must never be committed.
 */
public final class EnvConfig {

    private static final Logger LOGGER = Logger.getLogger(EnvConfig.class.getName());

    private static final String ENV_FILE_NAME = ".env";
    private static final String COMMENT_PREFIX = "#";

    private static Map<String, String> dotEnvValues;

    private EnvConfig() {
    }

    /**
     * Returns a configuration value, or {@code null} if it is not set anywhere.
     *
     * @param envVar the environment variable name, e.g. "GOOGLE_MAPS_API_KEY"
     * @param systemProperty the JVM system property name, e.g. "google.maps.api.key"
     * @return the configured value, trimmed, or {@code null} if absent everywhere
     */
    public static String get(String envVar, String systemProperty) {
        String value = trimToNull(System.getenv(envVar));

        if (value == null && systemProperty != null) {
            value = trimToNull(System.getProperty(systemProperty));
        }
        if (value == null) {
            value = trimToNull(dotEnv().get(envVar));
        }
        return value;
    }

    private static synchronized Map<String, String> dotEnv() {
        if (dotEnvValues == null) {
            dotEnvValues = loadDotEnv(new File(AppPaths.baseDir(), ENV_FILE_NAME));
        }
        return dotEnvValues;
    }

    private static Map<String, String> loadDotEnv(File file) {
        final Map<String, String> values = new HashMap<>();

        if (file.isFile()) {
            try {
                final List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                for (String line : lines) {
                    parseLine(line, values);
                }
            }
            catch (final IOException exception) {
                LOGGER.log(Level.WARNING, "Failed to read " + file.getPath(), exception);
            }
        }
        return values;
    }

    private static void parseLine(String line, Map<String, String> values) {
        final String trimmed = line.trim();
        final int separator = trimmed.indexOf('=');

        if (!trimmed.isEmpty() && !trimmed.startsWith(COMMENT_PREFIX) && separator > 0) {
            final String name = trimmed.substring(0, separator).trim();
            final String value = unquote(trimmed.substring(separator + 1).trim());
            if (!name.isEmpty()) {
                values.put(name, value);
            }
        }
    }

    private static String unquote(String value) {
        String result = value;
        final boolean quoted = result.length() >= 2
                && (result.startsWith("\"") && result.endsWith("\"")
                    || result.startsWith("'") && result.endsWith("'"));
        if (quoted) {
            result = result.substring(1, result.length() - 1);
        }
        return result;
    }

    private static String trimToNull(String value) {
        String result = null;
        if (value != null && !value.isBlank()) {
            result = value.trim();
        }
        return result;
    }
}

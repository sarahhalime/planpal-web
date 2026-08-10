package data_access;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

/** 
 * Reads the google client id and secret from an env.
 */
public final class GoogleOauthConfig {

    private static final Logger LOGGER = Logger.getLogger(GoogleOauthConfig.class.getName());

    private GoogleOauthConfig() {
    }

    /**
     * Gets the google client id.
     * @return the client id, or null if its not set anywhere
     */
    public static String getClientId() {
        return resolve("GOOGLE_OAUTH_CLIENT_ID", "google.oauth.client.id",
                "google-oauth-client-id.txt");
    }

    /**
     * Gets the google client secret.
     * @return the client secret, or null if its not set
     */
    public static String getClientSecret() {
        return resolve("GOOGLE_OAUTH_CLIENT_SECRET", "google.oauth.client.secret",
                "google-oauth-client-secret.txt");
    }

    private static String resolve(String envVar, String systemProperty, String fileName) {
        String resolved = EnvConfig.get(envVar, systemProperty);
        if (resolved == null) {
            resolved = readFile(fileName);
        }
        return resolved;
    }

    private static String readFile(String fileName) {
        String value = null;
        final File file = AppPaths.resolve(fileName);
        if (file.exists()) {
            try {
                final String contents = Files.readString(file.toPath()).trim();
                if (!contents.isBlank()) {
                    value = contents;
                }
            }
            catch (final IOException exception) {
                LOGGER.log(Level.WARNING, "Failed to read " + file.getPath(), exception);
            }
        }
        return value;
    }
}

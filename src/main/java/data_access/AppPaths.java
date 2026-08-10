package data_access;

import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;

/**
 * Resolves the location of the PlanPalDatabase folder that holds the application's
 * local data and credential files.
 *
 * <p>Checked in order:
 * <ol>
 *     <li>The {@code planpal.data.dir} JVM system property, settable per-run with
 *     {@code -Dplanpal.data.dir=...}.</li>
 *     <li>A {@code PlanPalDatabase} folder in the current working directory. This is the
 *     case when running from the project root inside an IDE.</li>
 *     <li>A {@code PlanPalDatabase} folder beside the running JAR. This is the case when
 *     the packaged application is launched from somewhere else, such as by double-clicking
 *     it in a file browser.</li>
 * </ol>
 */
public final class AppPaths {

    private static final String DATABASE_FOLDER = "PlanPalDatabase";
    private static final String ENV_FILE_NAME = ".env";
    private static final String DATA_DIR_SYSTEM_PROPERTY = "planpal.data.dir";

    private AppPaths() {
    }

    /**
     * Returns the folder holding the application's data files, creating it if this is the
     * first time the application has run.
     *
     * @return the PlanPalDatabase folder
     */
    public static File databaseDir() {
        final File dir = locateDatabaseDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * Resolves a data file name to its location inside the PlanPalDatabase folder.
     *
     * @param fileName the file's name, e.g. "events.json"
     * @return that file inside the PlanPalDatabase folder
     */
    public static File resolve(String fileName) {
        return new File(databaseDir(), fileName);
    }

    /**
     * Returns the folder the application treats as its home: the one holding the
     * {@code .env} file and the PlanPalDatabase folder.
     *
     * <p>This is the current working directory when the application is started from the
     * project root, as an IDE does, and the folder holding the JAR otherwise.
     *
     * @return the application's home folder
     */
    public static File baseDir() {
        final File workingDir = new File("").getAbsoluteFile();
        final String configuredDir = System.getProperty(DATA_DIR_SYSTEM_PROPERTY);
        final File result;

        if (configuredDir != null && !configuredDir.isBlank()) {
            // An explicit data directory also holds the configuration, so that overriding
            // the location moves the data files and the .env file together.
            result = new File(configuredDir.trim());
        }
        else if (new File(workingDir, DATABASE_FOLDER).isDirectory()
                || new File(workingDir, ENV_FILE_NAME).isFile()) {
            result = workingDir;
        }
        else {
            result = jarDirOrDefault(workingDir);
        }
        return result;
    }

    private static File locateDatabaseDir() {
        final File result;
        final String configuredDir = System.getProperty(DATA_DIR_SYSTEM_PROPERTY);

        if (configuredDir != null && !configuredDir.isBlank()) {
            result = new File(configuredDir.trim());
        }
        else {
            result = new File(baseDir(), DATABASE_FOLDER);
        }
        return result;
    }

    private static File jarDirOrDefault(File fallback) {
        File result = fallback;
        final CodeSource codeSource = AppPaths.class.getProtectionDomain().getCodeSource();

        if (codeSource != null && codeSource.getLocation() != null) {
            try {
                final File jarDir = new File(codeSource.getLocation().toURI()).getParentFile();
                if (jarDir != null) {
                    result = jarDir;
                }
            }
            catch (final URISyntaxException | IllegalArgumentException exception) {
                result = fallback;
            }
        }
        return result;
    }
}

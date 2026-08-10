package data_access;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Small helper for reading/writing a JSONArray to/from a file on disk.
 * Returns an empty JSONArray if the file doesn't exist yet (first run).
 */
final class JsonFileUtil {

    private JsonFileUtil() {
    }

    /**
     * Resolves a data file name to its full path inside the PlanPalDatabase folder,
     * creating the folder if this is the first time the app has run.
     *
     * @param fileName the JSON file's name, e.g. "events.json"
     * @return the full relative path to that file
     */
    static String resolvePath(String fileName) {
        return AppPaths.resolve(fileName).getPath();
    }

    static JSONArray readArray(String filePath) {
        final JSONArray result;
        final File file = new File(filePath);
        if (!file.exists()) {
            result = new JSONArray();
        }
        else {
            try (InputStream inputStream = new FileInputStream(file)) {
                result = new JSONArray(new JSONTokener(inputStream));
            }
            catch (IOException exception) {
                throw new RuntimeException("Failed to read data file: " + filePath, exception);
            }
        }
        return result;
    }

    static void writeArray(String filePath, JSONArray array) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(array.toString(2));
        }
        catch (IOException exception) {
            throw new RuntimeException("Failed to write data file: " + filePath, exception);
        }
    }

    /**
     * Reads a JSON object from the given file. Returns an empty JSONObject if the file
     * does not exist yet (e.g. the first time the application runs).
     *
     * @param filePath path to the JSON file on disk
     * @return the parsed JSONObject, or an empty one if the file is missing
     */
    static JSONObject readObject(String filePath) {
        JSONObject result = new JSONObject();
        final File file = new File(filePath);
        if (file.exists()) {
            try (InputStream inputStream = new FileInputStream(file)) {
                result = new JSONObject(new JSONTokener(inputStream));
            }
            catch (IOException exception) {
                System.err.println("Failed to read data file: " + filePath);
            }
        }
        return result;
    }

    /**
     * Writes a JSON object to the given file, overwriting whatever was there before.
     *
     * @param filePath path to the JSON file on disk
     * @param object the JSONObject to persist
     */
    static void writeObject(String filePath, JSONObject object) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(object.toString(2));
        }
        catch (IOException exception) {
            System.err.println("Failed to write data file: " + filePath);
        }
    }
}

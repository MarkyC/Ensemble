package ca.yorku.cirillom.ensemble.preferences;

import java.io.*;
import java.util.List;
import java.util.Properties;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/9/14 7:07 PM.
 * Preferences is a Singleton object that stores user preferences
 */

public class Preferences {

    /**
     * Filename of the Preferences file
     */
    public static final String FILENAME = "ensemble.properties";

    /**
     * Models preference field, controls which models are automatically loaded
     */
    public static final String MODELS = "models";

    // Default loaded models
    private static final String DEFAULT_MODELS = "moving-average";

    // Encapsulated Properties class
    private Properties prop = new Properties();

    private static Preferences ourInstance = new Preferences();

    public static Preferences getInstance() {
        return ourInstance;
    }

    private Preferences() {

        File prefs = new File(FILENAME);
        if (prefs.exists()) {
            load();
        } else {
            create();
        }

        // Check that default models are set
        if (!prop.containsKey(MODELS)) {
            put(MODELS, DEFAULT_MODELS);
        }
    }

    /**
     * Load a Properties file
     */
    private void load() {

        try (InputStream input = new FileInputStream(FILENAME)) {

            // load a properties file
            prop.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Create a Properties file
     */
    private void create() {
        put(MODELS, DEFAULT_MODELS);

        save();
    }

    /**
     * Saves user preferences to a properties File
     */
    public void save() {
        try (OutputStream output = new FileOutputStream(FILENAME)) {

            prop.store(output, "Ensemble Properties");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Adds/Updates a Preference item
     * @param key - Key
     * @param value - Value
     */
    public String put(String key, String value) {
        return (String) prop.put(key, value);
    }

    /**
     * Adds/Updates a Preference item
     * @param key - Key
     * @param values - Values as a List
     */
    public String put(String key, List<String> values) {

        // Don't insert empty list
        if (0 == values.size()) return null;

        String value = "";
        for (String v : values) {
            value += v + ", ";
        }
        value = value.substring(0, value.length() - 2); // get rid of last comma and space

        return put(key, value);
    }

    /**
     * Fetches a Preference Item
     * @param key - Key
     * @return Value associated with key, or null if no such value exists
     */
    public String get(String key) {
        return prop.get(key).toString();
    }

    /**
     * Removes a Preference Item
     * @param key - Key
     * @return The removed value
     */
    public String remove(String key) {
        return (String) prop.remove(key);
    }

    /**
     * Whether the Preferences file has a field for a specified key
     * @param key - The key to search for
     * @return true if found, false otherwise
     */
    public boolean has(String key) {
        return prop.containsKey(key);
    }
}
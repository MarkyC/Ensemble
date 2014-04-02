package ca.yorku.cirillom.ensemble.preferences;

import ca.yorku.cirillom.ensemble.modellers.LinearRegressionModel;
import ca.yorku.cirillom.ensemble.modellers.MovingAverageModel;

import java.io.*;
import java.util.*;

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
    public static final String ALL_MODELS = "all-models";
    public static final String ENABLED_MODELS = "enabled-models";

    public static final String MOVING_AVERAGE       = MovingAverageModel.class.getSimpleName();
    public static final String LINEAR_REGRESSION    = LinearRegressionModel.class.getSimpleName();

    private static final String DEFAULT_MODELS = MOVING_AVERAGE + ", " + LINEAR_REGRESSION;

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
        if (!prop.containsKey(ALL_MODELS)) {
            put(ALL_MODELS, DEFAULT_MODELS);
        }

        // Check that default models are enabled
        if (!prop.containsKey(ENABLED_MODELS)) {
            put(ENABLED_MODELS, DEFAULT_MODELS);
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
        put(ALL_MODELS, DEFAULT_MODELS);
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

    public String append(String key, String value) {
        Set<String> values = getAsSet(key);
        values.add(value);
        return put(key, values.toArray(new String[1]));
    }

    /**
     * Adds/Updates a Preference item
     * @param key - Key
     * @param value - Value
     */
    public synchronized String put(String key, String value) {
        return (String) prop.put(key, value);
    }

    /**
     * Adds/Updates a Preference item
     * @param key - Key
     * @param values - Values as a List
     */
    public String put(String key, Set<String> values) {
        String value = "";
        for (String v : values) {
            value += v + ", ";
        }

        // get rid of last comma and space
        if (value.length() > 2) value = value.substring(0, value.length() - 2);

        // get rid of leading comma
        if ( (value.length() > 0) && (value.charAt(0) == ',') ) value = value.substring(1);

        return put(key, value.trim());
    }

    /**
     * Adds/Updates a Preference item
     * @param key - Key
     * @param values - Values as a List
     */
    public String put(String key, String[] values) {
        Set<String> newVals = new HashSet<>();
        for(String val : values) {
            if (null != val) newVals.add(val.trim());
        }
        return put(key, newVals);
    }


    /**
     * Fetches a Preference Item
     * @param key - Key
     * @return Value associated with key, or null if no such value exists
     */
    public synchronized String get(String key) {
        return prop.get(key).toString().trim();
    }
    public String[] getAsArray(String key) {
        return get(key).split(", ");
    }
    public List<String> getAsList(String key) {
        return Arrays.asList(get(key).split(", "));
    }
    public Set<String> getAsSet(String key) {
        return new HashSet<>(getAsList(key));
    }

    /**
     * Removes a Preference Item
     * @param key - Key
     * @return The removed value
     */
    public synchronized String remove(String key) {
        return (String) prop.remove(key);
    }

    /**
     * Removes a value from a Preference Item
     * e.g., If models = one,two,three and I call removeFromKey(models, two),
     * I am left with models = one, three
     * @param key
     * @param value
     * @return
     */
    public synchronized String removeFromKey(String key, String value) {
        Set<String> values = getAsSet(key);
        values.remove(value);
        return put(key, values.toArray(new String[1]));
    }

    /**
     * Whether the Preferences file has a field for a specified key
     * @param key - The key to search for
     * @return true if found, false otherwise
     */
    public synchronized boolean has(String key) {
        return prop.containsKey(key);
    }
}

import java.util.Locale;
import java.util.Properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Class for managing configuration data stored in a properties file.
 */
public class ConfigManager {

    /** 
     * The timeframe value. Determines the amount of days that the timetable can be away from. 
     * Use with caution. High values can cause very long scraping time.
     */
    public final int timeframe;
    /** The login read from the configuration file. */
    public final String login;
    /** The password read from the configuration file. */
    public final String password;
    /** Argument for the webdriver. Can be "--headless", anything else is ignored. */
    public final String arg;
    /** The timestamp read from the configuration file. Either empty or date of the saved timetable. */
    public final String timestamp;

    /**
     * Constructs a new Config object by reading configuration data from the specified file path.
     *
     * @param configPath The path to the configuration file.
     * @throws IOException If an I/O error occurs while reading the configuration file.
     */
    public ConfigManager(final String configPath) throws IOException {
        Properties config = new Properties();

        try (final FileInputStream fis = new FileInputStream(configPath)) {
            config.load(fis);
            timeframe = Integer.parseInt(get("timeframe", config)) > 0 ? Integer.parseInt(get("timeframe", config)) : 0;
            login =     get("login",          config);
            password =  get("password",       config);
            arg =       get("arg",            config);
            timestamp = get("timestamp",      config);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Could not find .properties file in specified path.");
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid value at \"timeframe\" property.");
        }
    }

    /**
     * Saves the provided timestamp to the configuration file.
     *
     * @param timestamp       The timestamp to be saved.
     * @param configPath      The path to the configuration file.
     * @throws IOException    If an I/O error occurs while writing to the configuration file.
     */
    public static void saveTimestamp(final String timestamp, final String configPath) throws IOException { 
        Properties config = new Properties();
        
        try (final FileInputStream fis = new FileInputStream(configPath)) {
            config.load(fis);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Could not find .properties file in specified path.");
        }

        config.setProperty("timestamp", timestamp);

        try (final FileOutputStream fos = new FileOutputStream(configPath)) {
            config.store(fos, null);
        } catch (IOException e) {
            throw new IOException("Could not update date in .properties file.");
        }
    }

    /**
     * Formats the provided timestamp string to a LocalDate object.
     *
     * @param timestamp The timestamp string to be formatted.
     * @return          The formatted LocalDate object.
     */
    public static LocalDate formatToLocalDate(final String timestamp) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d LLLL yyyy", new Locale("pl"));
            return LocalDate.parse(timestamp, formatter);
        
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException("Invalid date passed to the method.", timestamp, 0);
        }
    }

    private String get(final String property, final Properties config) {
        String value = config.getProperty(property);
        if (value == null) {
            throw new IllegalArgumentException("Property not found: " + property);
        } 
        return value;
    }
}
import java.util.Locale;
import java.util.Properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Class for managing configuration data stored in a properties file.
 */
public class ConfigManager {

    /** The timeframe value read from the configuration file. */
    public final int timeframe;
    /** The login username read from the configuration file. */
    public final String login;
    /** The login password read from the configuration file. */
    public final String password;
    /** Additional argument for the web driver read from the configuration file. */
    public final String arg;
    /** The date read from the configuration file. */
    public final String date;

    /**
     * Constructs a new Config object by reading configuration data from the specified file path.
     *
     * @param configPath The path to the configuration file.
     * @throws IOException If an I/O error occurs while reading the configuration file.
     */
    public ConfigManager(final String configPath) throws IOException {
        Properties config = new Properties();

        try (final FileInputStream is = new FileInputStream(configPath)) {
            config.load(is);
            timeframe = Integer.parseInt(get("timeframe", config)) > 0 ? Integer.parseInt(get("timeframe", config)) : 0;
            login =     get("login",     config);
            password =  get("password",  config);
            arg =       get("arg",       config);
            date =      get("date",      config);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Could not find .properties file in specified path.");
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid value at \"timeframe\" property.");
        }
    }

    /**
     * Saves the provided date to the configuration file.
     *
     * @param date       The date to be saved.
     * @param configPath The path to the configuration file.
     * @throws IOException If an I/O error occurs while writing to the configuration file.
     */
    public static void saveDate(final String date, final String configPath) throws IOException { // maybe LocalDate date?
        Properties config = new Properties();
        
        try (final FileInputStream is = new FileInputStream(configPath)) {
            config.load(is);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Could not find .properties file in specified path.");
        }

        config.setProperty("date", date);

        try (final FileOutputStream os = new FileOutputStream(configPath)) {
            config.store(os, null);
        } catch (IOException e) {
            throw new IOException("Could not update date in .properties file.");
        }
    }

    /**
     * Formats the provided date string to a LocalDate object.
     *
     * @param date The date string to be formatted.
     * @return The formatted LocalDate object.
     */
    public static LocalDate formatToLocalDate(final String date) {
        if (date.equals("")) { return null; }
        try {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d LLLL yyyy", new Locale("pl"));
            return LocalDate.parse(date, formatter);
        
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid date passed to the method.");
        }
    }

    private String get(final String property, final Properties config) {
        String value = config.getProperty(property);
        if (value != null) {
            return value;
        } else {
            throw new IllegalArgumentException("Property not found: " + property);
        }
    }
}

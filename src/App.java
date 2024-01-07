import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.NoSuchElementException;
import java.util.Locale;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.*;

/*
 * TODO:
 * 1. add gui
 * 2. add caching
 * 3. add config
 * 4. add serialization!
 */

public class App {
    private static String tryToGet(String property, Properties config) {
        try {
            String value = config.getProperty(property);
            return value;
        } catch (Exception e) {
            throw new NoSuchElementException("Unable to fetch " + property);
        }
    }

    private static void tryToSet(String property, String value, Properties config) {
        try {
            config.setProperty(property, value);
        } catch (Exception e) {
            throw new NoSuchElementException("Unable to set " + property);
        }
    }

    public static void main(String[] args) throws IOException {

        String[] headers = {"Time", "Subject"};
        List<List<String>> rows = new ArrayList<>();
        Properties config = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {

            config.load(input);
            LocalDate newestDate = LocalDate.parse(tryToGet("date", config));
            if (newestDate == null || newestDate.isBefore(LocalDate.now())) {
                String timeframe = tryToGet("timeframe", config);
                String login = tryToGet("login", config);
                String password = tryToGet("password", config);
                String arg = tryToGet("arg", config);
                List<List<String>> timetable = Scraper.timetableScraper(Integer.parseInt(timeframe), login, password, arg);

                try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("timetable.bin"))) {
                    os.writeObject(timetable);
                } catch (FileNotFoundException e) {
                    throw new FileNotFoundException("Unable to find timetable.bin file. It should be in src directory");
                } catch (IOException e) {
                    throw new IOException("Unable to write to timetable.bin file");
                }
                tryToSet("date", timetable.get(0).get(0), config);
            }
            else {

            }
            
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Could not locate the config.properties file");
        }
    }
}

import java.util.Locale;
import java.util.Properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Config {
    final private Properties CONFIG;
    final private String CONFIG_PATH;

    public final int timeframe;
    public final String login;
    public final String password;
    public final String arg;

    public Config(final String CONFIG_PATH) throws IOException {
        this.CONFIG = new Properties();
        this.CONFIG_PATH = CONFIG_PATH;

        try (final FileInputStream is = new FileInputStream(this.CONFIG_PATH)) {
            this.CONFIG.load(is);
            timeframe = Integer.parseInt(get("timeframe"));
            login = get("login");
            password = get("password");
            arg = get("arg");
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Could not find .properties file in specified path");
        }
    }

    
    public void updateDate(String date) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d LLLL yyyy", new Locale("pl"));
        String formatedDate = LocalDate.parse(date, formatter).toString();
        this.CONFIG.setProperty("date", formatedDate);

        try (final FileOutputStream os = new FileOutputStream(this.CONFIG_PATH)) {
            this.CONFIG.store(os, null);
        } catch (IOException e) {
            throw new IOException("Could not update date in .properties file.");
        }
    }


    private String get(String property) {
        String value = this.CONFIG.getProperty(property);
        if (value != null) {
            return value;
        } else {
            throw new IllegalArgumentException("Property not found: " + property);
        }
    }
}

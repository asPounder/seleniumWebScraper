import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.*;

/*
 * TODO:
 * 1. add gui
 * 2. add caching
 * 3. add config
 * 4. add serialization!
 * 5. update Scraper with try-final.
 */

public class App {
    private static String tryToGet(String property, Properties config) {
        try {
            String value = config.getProperty(property);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            throw new NoSuchElementException("Unable to fetch " + property);
        }
    }

    private static void tryToSet(String property, String value, Properties config) {
        try {
            config.setProperty(property, value);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NoSuchElementException("Unable to set " + property);
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, ExecutionException {
        boolean isOutdated;
        List<List<String>> timetable = null;
        JFrame frame = new JFrame("Plan lekcji");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Properties config = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {

            config.load(input);
            LocalDate newestDate = LocalDate.parse(tryToGet("date", config));
            if (newestDate == null || newestDate.isBefore(LocalDate.now())) {

                isOutdated = true;
                final int timeframe = Integer.parseInt(tryToGet("timeframe", config));
                final String login = tryToGet("login", config);
                final String password = tryToGet("password", config);
                final String arg = tryToGet("arg", config);

                @SuppressWarnings("all")
                SwingWorker<List<List<String>>, Void> scraper = new SwingWorker() {
                    @Override
                    protected List<List<String>> doInBackground() {
                        return Scraper.timetableScraper(timeframe, login, password, arg);
                    }
                };
                scraper.execute();
                while (!scraper.isDone()) {

                }
                try {
                    timetable = scraper.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ExecutionException("timetableScraper failed.", e.getCause());
                }
                
            }
            else {

                isOutdated = false;
                try (ObjectInputStream os = new ObjectInputStream(new FileInputStream("timetable.bin"))) {
                    timetable = (List<List<String>>) os.readObject();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    throw new FileNotFoundException("Unable to find timetable.bin file. It should be in src directory");
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new IOException("Unable to fetch from timetable.bin file");
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new FileNotFoundException("Could not locate the config.properties file");
        }

        String[][] tableDate = new String[timetable.size()][2];
        for (int i = 1; i < timetable.size(); i++) {
            tableDate[i][0] = timetable.get(i).get(0);
            tableDate[i][1] = timetable.get(i).get(1);
        }



        if (isOutdated) {
            try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("timetable.bin"))) {
                os.writeObject(timetable);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new FileNotFoundException("Unable to find timetable.bin file. It should be in src directory");
            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException("Unable to write to timetable.bin file");
            }
            tryToSet("date", timetable.get(0).get(0), config);
            try (FileOutputStream fo = new FileOutputStream("config.properties")) {
                config.store(fo, null);
            } catch (FileNotFoundException e){
                e.printStackTrace();
                throw new FileNotFoundException("Could not locate the config.properties file");
            }
        } else {

        }
    }
}

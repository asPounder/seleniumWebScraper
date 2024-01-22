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
import java.time.format.DateTimeParseException;

import javax.swing.*;
import java.awt.GridBagLayout;
import java.awt.Font;

/*
 * TODO:
 * 1. add gui
 * 2. add caching
 * 3. add config
 * 4. add serialization!
 * 5. update Scraper with try-final.
 * 6. add magic number constants.
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

    public static void main(String[] args) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {

        final String CONFIG_PATH = System.getProperty("user.dir") + "/src/config.properties";
        final String BINARY_PATH = System.getProperty("user.dir") + "/src/timetable.bin";
        final String[] LOADING_STATES = {"Loading", "Loading.", "Loading..", "Loading..."};

        boolean isOutdated;
        List<List<String>> timetable = null;
        int loading_id = 0;
        LocalDate newestDate;

        JFrame frame = new JFrame("Plan lekcji");
        JPanel panel = new JPanel();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setSize(300, 300);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new GridBagLayout());
        frame.add(panel);

        Properties config = new Properties();
        try (FileInputStream input = new FileInputStream(CONFIG_PATH)) {

            config.load(input);
            try {
                newestDate = LocalDate.parse(tryToGet("date", config));
            } catch (DateTimeParseException e) {
                newestDate = null;
            }
            
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
                JLabel label = new JLabel("Loading");
                label.setFont(new Font("Arial", Font.PLAIN, 24));
                panel.add(label);

                scraper.execute();
                while (!scraper.isDone()) {
                    label.setText(LOADING_STATES[loading_id % 4]);
                    loading_id += 1;
                    Thread.sleep(500);
                }
                try {
                    timetable = scraper.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    frame.dispose();
                    throw new ExecutionException("timetableScraper failed.", e.getCause());
                }
                
            }
            else {

                isOutdated = false;
                try (ObjectInputStream os = new ObjectInputStream(new FileInputStream(BINARY_PATH))) {
                    timetable = (List<List<String>>) os.readObject();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    frame.dispose();
                    throw new FileNotFoundException("Unable to find timetable.bin file. It should be in src directory");
                } catch (IOException e) {
                    e.printStackTrace();
                    frame.dispose();
                    throw new IOException("Unable to fetch from timetable.bin file");
                }

            }
        } catch (FileNotFoundException e) {
            frame.dispose();
            e.printStackTrace();
            throw new FileNotFoundException("Could not locate the config.properties file");
        }


        String[][] tableDate = new String[timetable.size()][2];
        for (int i = 1; i < timetable.size(); i++) {
            tableDate[i][0] = timetable.get(i).get(0);
            tableDate[i][1] = timetable.get(i).get(1);
        }



        if (isOutdated) {
            try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(BINARY_PATH))) {
                os.writeObject(timetable);
            } catch (FileNotFoundException e) {
                frame.dispose();
                e.printStackTrace();
                throw new FileNotFoundException("Unable to find timetable.bin file. It should be in src directory");
            } catch (IOException e) {
                frame.dispose();
                e.printStackTrace();
                throw new IOException("Unable to write to timetable.bin file");
            }
            tryToSet("date", timetable.get(0).get(0), config);
            try (FileOutputStream fo = new FileOutputStream("config.properties")) {
                config.store(fo, null);
            } catch (FileNotFoundException e){
                frame.dispose();
                e.printStackTrace();
                throw new FileNotFoundException("Could not locate the config.properties file");
            }
        } else {

        }
    }
}

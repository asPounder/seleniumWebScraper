import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;

import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.JTableHeader;
import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.Font;
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

    public static void main(String[] args) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {

        final String CONFIG_PATH = System.getProperty("user.dir") + "/src/config.properties";
        final String BINARY_PATH = System.getProperty("user.dir") + "/src/timetable.bin";
        final String[] LOADING_STATES = {"Loading", "Loading.", "Loading..", "Loading..."};

        boolean isOutdated;
        List<List<String>> timetable = null;
        int loading_id = 0;
        LocalDate newestDate;

        JFrame frame = new JFrame("Plan lekcji");
        JPanel panel = new JPanel(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setSize(500, 300);
        frame.add(panel);

        try {
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
                        throw new ExecutionException("timetableScraper failed.", e.getCause());
                    }
                    panel.remove(label);
                    
                }
                else {
                    isOutdated = false;
                    try (ObjectInputStream os = new ObjectInputStream(new FileInputStream(BINARY_PATH))) {
                        timetable = (List<List<String>>) os.readObject();
                    } catch (FileNotFoundException e) {
                        throw new FileNotFoundException("Unable to find timetable.bin file. It should be in src directory");
                    } catch (IOException e) {
                        throw new IOException("Unable to fetch from timetable.bin file");
                    }

                }
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException("Could not locate the config.properties file");
            }


            String[] headerData = {"przedmiot", " godzina"};
            String[][] tableData = new String[timetable.size()-1][2];
            for (int i = 1; i < timetable.size(); i++) {
                tableData[i-1][0] = timetable.get(i).get(0);
                tableData[i-1][1] = timetable.get(i).get(1);
            }
            JPanel tablePanel = new JPanel();
            tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
            JTable table = new JTable(tableData, headerData);
            JTableHeader header = table.getTableHeader();

            Color color = UIManager.getColor("Table.gridColor");
            MatteBorder headerBorder = new MatteBorder(1, 1, 0, 1, color);
            MatteBorder tableBorder = new MatteBorder(0, 1, 1, 1, color);
            table.setBorder(tableBorder);
            header.setBorder(headerBorder);

            table.getColumnModel().getColumn(0).setPreferredWidth(270);
            table.getColumnModel().getColumn(1).setPreferredWidth(90);
            table.setRowHeight(30);

            tablePanel.add(header);
            tablePanel.add(table);
            panel.add(tablePanel);
            panel.revalidate();
            panel.repaint();


            if (isOutdated) {
                try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(BINARY_PATH))) {

                os.writeObject(timetable);
                } catch (FileNotFoundException e) {
                    throw new FileNotFoundException("Unable to find timetable.bin file. It should be in src directory");
                } catch (IOException e) {
                    throw new IOException("Unable to write to timetable.bin file");
                }
                
                System.out.println(timetable.get(0).get(0).equals("27 styczeń 2024"));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d LLLL yyyy", new Locale("pl"));
                String timetableDate = LocalDate.parse(timetable.get(0).get(0), formatter).toString();
                tryToSet("date", timetableDate, config);
                try (FileOutputStream fo = new FileOutputStream(CONFIG_PATH)) {
                    config.store(fo, null);
                } catch (FileNotFoundException e){
                    throw new FileNotFoundException("Could not locate the config.properties file");
                }
            }

        } catch (Exception e) {
            frame.dispose();
            e.printStackTrace();
            throw e;
        }
    }
}

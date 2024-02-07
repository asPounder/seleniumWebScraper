import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Locale;

public class Timetable {
    private String date;
    private String[][] timetableData;
    private String BINARY_PATH;
    private String CONFIG_PATH;

    public Timetable(String BINARY_PATH, String CONFIG_PATH) {
        this.BINARY_PATH = BINARY_PATH;
        this.CONFIG_PATH = CONFIG_PATH;
    }


    public int load() {
        try (ObjectInputStream os = new ObjectInputStream(new FileInputStream(this.BINARY_PATH))) {
            List<List<String>> timetable = (List<List<String>>) os.readObject();
            this.date = timetable.get(0).get(0);
            this.timetableData = format(timetable);
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }


    public static String[][] format(List<List<String>> timetable) {
        String[][] formatted = new String[timetable.size()-1][2];
        for (int i = 1; i < timetable.size(); i++) {
            formatted[i-1][0] = timetable.get(i).get(0);
            formatted[i-1][1] = timetable.get(i).get(1);
        }

        return formatted;
    }


    public String getDate() {
        if (this.date == null) {
            throw new IllegalStateException("Unable to get date. Call load() first.");
        } else {
            return this.date;
        }
    }


    public String[][] getTimetableData() {
        if (this.date == null) {
            throw new IllegalStateException("Unable to get timetableData. Call load() first.");
        } else {
            return this.timetableData;
        }
    }


    public void save(final String DATE) throws IOException {
        saveTimetableData();
        saveTimetableDate(DATE);
    }


    private void saveTimetableData() throws IOException {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(this.BINARY_PATH))) {
            os.writeObject(this.timetableData);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Unable to find timetable.bin file. It should be in src directory");
        } catch (IOException e) {
            throw new IOException("Unable to write to timetable.bin file");
        }
    }


    private void saveTimetableDate(final String DATE) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d LLLL yyyy", new Locale("pl"));
        String timetableDate = LocalDate.parse(DATE, formatter).toString();

        try (FileOutputStream fo = new FileOutputStream(this.CONFIG_PATH)) {
            Properties config = new Properties();
            config.setProperty("date", timetableDate);
            config.store(fo, null);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Unable to find config.properties file. It should be in src directory");
        } catch (IOException e) {
            throw new IOException("Unable to write to config.properties file");
        }
    }
}
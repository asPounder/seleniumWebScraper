import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        final String CONFIG_PATH = System.getProperty("user.dir") + "/src/config.properties";
        final String BINARY_PATH = System.getProperty("user.dir") + "/src/timetable.bin";

        Config cfg = new Config(CONFIG_PATH);
        LocalDate date = Config.formatToLocalDate(cfg.date);
        Gui gui = new Gui();
        TimetableData td = new TimetableData();
        boolean wasScrapped;

        try {
            if (date == null || date.isBefore(LocalDate.now())) {
                wasScrapped = true;
                td = gui.getTimetableData(CONFIG_PATH);
            } else {
                wasScrapped = false;
                td.timetable = TimetableUtils.deserializeTimetable(BINARY_PATH);
                if (td.timetable == null) {
                    td = gui.getTimetableData(CONFIG_PATH);
                }
            }

            gui.displaytimetable(td.timetable);

            if (wasScrapped) {
                TimetableUtils.serializeTimetable(td.timetable, BINARY_PATH);
                Config.saveDate(td.date.toString(), CONFIG_PATH);
            }
        } finally {
            gui.close();
        }
    }   
}
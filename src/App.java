import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

/**
 * The main application class responsible for initializing and running the timetable application.
 */
public class App {
    /**
     * The main entry point of the application.
     *
     * @param args The command-line arguments passed to the application.
     * @throws IOException            If an I/O error occurs while reading or writing files.
     * @throws InterruptedException   If the execution is interrupted while waiting for a result.
     * @throws ExecutionException     If an exception occurs during the execution of a task.
     */
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        final String CONFIG_PATH = System.getProperty("user.dir") + "/src/config.properties";
        final String BINARY_PATH = System.getProperty("user.dir") + "/src/timetable.bin";
        
        ConfigManager cfg = new ConfigManager(CONFIG_PATH);
        LocalDate date = ConfigManager.formatToLocalDate(cfg.date);
        GuiManager gui = new GuiManager();
        TimetableData timetableData = new TimetableData();
        boolean wasScrapped;

        try {
            if (date == null || date.isBefore(LocalDate.now())) {
            wasScrapped = true;
            timetableData = gui.getTimetableData(CONFIG_PATH);
            } else {
                wasScrapped = false;
                timetableData.timetable = TimetableUtils.deserializeTimetable(BINARY_PATH);
                if (timetableData.timetable == null) {
                    timetableData = gui.getTimetableData(CONFIG_PATH);
            }
        }
        
        gui.displaytimetable(timetableData.timetable);
        
            if (wasScrapped) {
            TimetableUtils.serializeTimetable(timetableData.timetable, BINARY_PATH);
                ConfigManager.saveDate(timetableData.date.toString(), CONFIG_PATH);
            }
        } finally {
            gui.close();
        }
    }   
}
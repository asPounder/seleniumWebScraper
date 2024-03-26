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
        final String CONFIG_PATH = "src/main/resources/config.properties";
        final String BINARY_PATH = "src/main/resources/timetable.bin";
        
        ConfigManager cfgm = new ConfigManager(CONFIG_PATH);
        LocalDate date = cfgm.date.equals("") ? null : LocalDate.parse(cfgm.date);
        GuiManager gui = new GuiManager();
        TimetableData timetableData = new TimetableData();
        boolean wasScrapped;

        try {
            if (date == null || date.isBefore(LocalDate.now())) {
                wasScrapped = true;
                timetableData = gui.getTimetableData(cfgm.timeframe, cfgm.login, cfgm.password, cfgm.arg);
            } else {
                wasScrapped = false;
                timetableData.timetable = TimetableManager.deserializeTimetable(BINARY_PATH);
                if (timetableData.timetable == null) {
                    wasScrapped = true;
                    timetableData = gui.getTimetableData(cfgm.timeframe, cfgm.login, cfgm.password, cfgm.arg);
                }
            }
        
            gui.displaytimetable(timetableData.timetable);
        
            if (wasScrapped) {
                TimetableManager.serializeTimetable(timetableData.timetable, BINARY_PATH);
                    ConfigManager.saveDate(timetableData.date.toString(), CONFIG_PATH);
                }

        } catch (Exception e) {
            throw e;
        } finally {
            // gui.close();
        }
    }   
}
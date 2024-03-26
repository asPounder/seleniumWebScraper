import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * Utility class for serializing, deserializing, and formatting timetable data.
 */
public class TimetableManager {

    /**
     * Deserialize timetable data from a binary file.
     *
     * @param binaryPath The path to the binary file containing the serialized timetable data.
     * @return The deserialized timetable data as a two-dimensional array of strings, or {@code null} if an error occurs.
     */
    public static String[][] deserializeTimetable(final String binaryPath) {
        try (ObjectInputStream os = new ObjectInputStream(new FileInputStream(binaryPath))) {
            return (String[][]) os.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Format a timetable from a list of lists to a two-dimensional array.
     *
     * @param timetable The timetable data as a list of lists, where each inner list represents a row of the timetable.
     * @return The formatted timetable data as a two-dimensional array of strings.
     */
    public static String[][] formatTimetable(List<List<String>> timetable) {
        String[][] formatted = new String[timetable.size()][2]; // was timetable.size()-1, could be needed?
        for (int i = 0; i < timetable.size(); i++) {
            formatted[i][0] = timetable.get(i).get(0);
            formatted[i][1] = timetable.get(i).get(1);
        }
        return formatted;
    }

    /**
     * Serialize timetable data to a binary file.
     *
     * @param timetable  The timetable data to be serialized, represented as a two-dimensional array of strings.
     * @param binaryPath The path to the binary file where the serialized timetable data will be written.
     * @throws IOException If an I/O error occurs while writing the data to the file.
     */
    public static void serializeTimetable(final String[][] timetable, final String binaryPath) throws IOException {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(binaryPath))) {
            os.writeObject(timetable);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Unable to find timetable.bin file. It should be in src directory");
        } catch (IOException e) {
            throw new IOException("Unable to write to timetable.bin file");
        }
    }
}
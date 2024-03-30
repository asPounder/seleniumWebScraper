import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

class TimetableManagerTest {
    static final String PATH = "src/test/resources/timetable.bin";
    static final String[][] ARRAY_TIMETABLE = {
        {"Chemia", "12:30"},
        {"Fizyka", "14:30"},
        {"Matematyka", "16:30"},
        {"Informatyka", "18:30"}
    };
    final static List<List<String>> LIST_TIMETABLE = List.of(
        List.of("Chemia", "12:30"),
        List.of("Fizyka", "14:30"),
        List.of("Matematyka", "16:30"),
        List.of("Informatyka", "18:30")
    );

    @BeforeAll
    static void init() throws IOException {
        new File(PATH).createNewFile();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PATH))) {
            oos.writeObject(ARRAY_TIMETABLE);
        }
    }
    
    @Test
    @DisplayName("Testing deserializing timetable.")
    void correctDeserializeTest() {
        String[][] deserializedTimetable = TimetableManager.deserializeTimetable(PATH);
        assertArrayEquals(ARRAY_TIMETABLE, deserializedTimetable);
    }

    @Test
    @DisplayName("Testing deserializing timetable with invalid path.")
    void IncorrectDeserializeTest() {
        assertEquals(null, TimetableManager.deserializeTimetable(""));      
    }

    @Test
    @DisplayName("Testing formatting timetable.")
    void formatTimetableTest() {
        assertArrayEquals(TimetableManager.formatTimetable(LIST_TIMETABLE), ARRAY_TIMETABLE);
    }

    @Test
    @DisplayName("Testing serializing timetable.")
    void correctSerializeTest() throws IOException, ClassNotFoundException {
        final String TEMP_PATH = "src/test/resources/temp.bin";
        new File(TEMP_PATH).createNewFile();

        TimetableManager.serializeTimetable(ARRAY_TIMETABLE, TEMP_PATH);

        String[][] serializedTimetable;
        try (ObjectInputStream os = new ObjectInputStream(new FileInputStream(TEMP_PATH))) {
            serializedTimetable = (String[][]) os.readObject();
        }

        assertArrayEquals(serializedTimetable, ARRAY_TIMETABLE);
        new File(TEMP_PATH).delete();
    }

    @Test
    @DisplayName("Testing serializing timetable with invalid path.")
    void IncorrectSerializeTest() {
        assertThrows(FileNotFoundException.class, () -> TimetableManager.serializeTimetable(ARRAY_TIMETABLE, ""));    
    }

    @AfterAll
    static void tearDown() {
        new File(PATH).delete();
    }
}
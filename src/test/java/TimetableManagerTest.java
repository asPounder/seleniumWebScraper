import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeAll;
import java.util.List;
import java.io.File;
import java.io.IOException;

@TestInstance(Lifecycle.PER_CLASS)
class TimetableManagerTest {
    final String PATH = "src/test/resources/timetable.bin";
    
    @BeforeAll
    void init() throws IOException {
        new File(PATH).createNewFile();
        List<List<String>> timetable = List.of(
            List.of("Chemia", "12:30"),
            List.of("Fizyka", "14:30"),
            List.of("Matematyka", "16:30"),
            List.of("Informatyka", "18:30")
        );
    }
    
    @Test
    @DisplayName("Testing deserializing timetable.")
    void correctDeserializeTest() {
        // TODO: implement
    }

    @Test
    @DisplayName("Testing deserializing timetable with invalid path.")
    void IncorrectDeserializeTest() {
        // TODO: implement        
    }

    @Test
    @DisplayName("Testing formatting timetable.")
    void formatTimetableTest() {
        // TODO: implement
    }

    @Test
    @DisplayName("Testing serializaing timetable.")
    void correctSerializeTest() {
        // TODO: implement
    }

    @Test
    @DisplayName("Testing serializing timetable with invalid path.")
    void IncorrectSerializeTest() {
        // TODO: implement        
    }
}
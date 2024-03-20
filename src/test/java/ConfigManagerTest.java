import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

@TestInstance(Lifecycle.PER_CLASS)
public class ConfigManagerTest {
    Properties cfg = new Properties();
    final String PATH = "src/test/resources/config.properties";

    @BeforeAll
    void createFile() throws IOException {
        new File(PATH).createNewFile();
        cfg.setProperty("date",     "");
        cfg.setProperty("timeframe","100");
        cfg.setProperty("password", "abcde12345");
        cfg.setProperty("arg",      "--headless");
        cfg.setProperty("login",    "abcde12345");
        try (FileOutputStream fos = new FileOutputStream(PATH)) {cfg.store(fos, null);};
    }

    
    @Nested
    @DisplayName("Reading test suite.")
    @TestInstance(Lifecycle.PER_CLASS)
    class ReadTest {

        @Test
        @DisplayName("Testing reading correct config file.")
        void correctConfigTest() throws IOException {
            ConfigManager cfgm = new ConfigManager(PATH);
            assertEquals(cfgm.date,     "");
            assertEquals(cfgm.timeframe,100);
            assertEquals(cfgm.password, "abcde12345");
            assertEquals(cfgm.arg,      "--headless");
            assertEquals(cfgm.login,    "abcde12345");
        }
    
        @Test
        @DisplayName("Testing reading invalid timeframe.")
        void invalidTimeframeTest() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(PATH)) {
                cfg.setProperty("timeframe", "a");
                cfg.store(fos, null);
                assertThrows(NumberFormatException.class, () -> new ConfigManager(PATH));
    
                cfg.setProperty("timeframe", "100");
                cfg.store(fos, null);
            }
        }
    
        @Test
        @DisplayName("Testing reading not present key.")
        void keyNotPresentTest() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(PATH)) {
                cfg.remove("timeframe");
                cfg.store(fos, null);
                assertThrows(IllegalArgumentException.class, () -> new ConfigManager(PATH));
    
                cfg.setProperty("timeframe", "100");
                cfg.store(fos, null);
            }
        }
        
    }

    @Test
    void writeTest() {

    }

    @AfterAll
    static void tearDownFile() {
        new File("src/test/resources/config.properties").delete();
    }

}

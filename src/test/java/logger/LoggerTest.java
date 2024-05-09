package logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LoggerTest {
    public static final String TEST_LOG_PATH = "./latest.log";
    public static final String STOPPED_TIME = "24.03.11 07:12";

    Logger logger;
    Date stoppedDate;
    String currentDate;

    @BeforeEach
    void setUp() throws ParseException {
        logger = Logger.getInstance(TEST_LOG_PATH);
        stoppedDate = new SimpleDateFormat(Logger.FORMAT_TIMESTAMP).parse(STOPPED_TIME);
        currentDate = new SimpleDateFormat(Logger.FORMAT_TIMESTAMP).format(new Date());

        new File(TEST_LOG_PATH).delete();
    }

    @AfterEach
    void tearDown() {
        new File(TEST_LOG_PATH).delete();
    }

    @ParameterizedTest
    @ValueSource (ints = {1, 3, 5, 6})
    void writeLog(int repeat) throws IOException {
        String content = "Hello World!";

        for (int i = 0; i < repeat; i++) {
            logger.writeLog(content);
        }

        List<String> actual = Files.readAllLines(Paths.get(TEST_LOG_PATH));

        assertEquals(repeat, actual.size());
        String expected = "[" + currentDate + "] writeLog()                    Hello World!";
        actual.forEach(log -> assertEquals(expected, log));
    }

    @Test
    void getLogFile() throws IOException {
        File file = logger.getLogFile();
        assertTrue(file.exists());
    }

    @Test
    void getFormattedTime() {
        assertEquals(STOPPED_TIME, logger.getFormattedTime(stoppedDate));
    }

    @Test
    void formatLogContent() {
        String actual = logger.formatLogContent("formatLogContent", "Hello World!");

        String expected = "[" + currentDate + "] formatLogContent()            Hello World!";
        assertEquals(expected, actual);
    }
}
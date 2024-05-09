package logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class LoggerTest {
    public static final String TEST_LOG_PATH = ".";
    public static final String TEST_LOG_FILE_NAME = "latest.log";
    public static final String TEST_LOG_FILE = TEST_LOG_PATH + "/" + TEST_LOG_FILE_NAME;
    public static final String STOPPED_TIME = "24.03.11 07:12";

    Logger logger;
    Date stoppedDate;
    String currentDate;

    @BeforeEach
    void setUp() throws ParseException {
        logger = Logger.getInstance(TEST_LOG_PATH);
        stoppedDate = new SimpleDateFormat(Logger.FORMAT_TIMESTAMP).parse(STOPPED_TIME);
        currentDate = new SimpleDateFormat(Logger.FORMAT_TIMESTAMP).format(new Date());

        new File(TEST_LOG_FILE).delete();
    }

    @AfterEach
    void tearDown() {
        new File(TEST_LOG_FILE).delete();
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
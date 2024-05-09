package shell;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static shell.Logger.MAX_LOG_BYTES;

@ExtendWith(MockitoExtension.class)
class LoggerTest {
    public static final String TEST_LOG_PATH = ".";
    public static final String TEST_LOG_FILE_NAME = "latest.log";
    public static final String TEST_LOG_FILE = TEST_LOG_PATH + "/" + TEST_LOG_FILE_NAME;
    public static final String STOPPED_TIME = "24.03.11 07:12";
    private static final String LATEST_LOG = "latest.log";
    private static final String PAST_LOG = "until_240101_00h_00m_00s.log";
    private static final String PAST_ZIP = "until_240101_00h_00m_00s.zip";

    Logger logger;
    Date stoppedDate;
    String currentDate;

    @TempDir
    Path tempDir;

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
    void rollLogFile() throws IOException, InterruptedException {
        Logger spyLogger = spy(Logger.class);

        doNothing().when(spyLogger).compress(any());
        doNothing().when(spyLogger).changeLogFile(any());

        File pastFile = createFile(tempDir.resolve(PAST_LOG));
        Thread.sleep(1);
        File lastestFile = createFile(tempDir.resolve(LATEST_LOG));

        setFileSize(lastestFile, MAX_LOG_BYTES + 1);
        spyLogger.rollLogFile(lastestFile);

        verify(spyLogger, times(1)).getPastLogFilesFrom(any());
        verify(spyLogger, times(1)).compress(any());
        verify(spyLogger, times(1)).changeLogFile(any());
    }

    @Test
    void isLogSizeFull() throws IOException {
        File file = createFile(tempDir.resolve(LATEST_LOG));
        assertThat(logger.isLogSizeFull(file)).isFalse();

        setFileSize(file, MAX_LOG_BYTES + 1);
        assertThat(logger.isLogSizeFull(file)).isTrue();
    }

    @Test
    void getPastLogFilesFromIfPathIsEmpty() throws IOException {
        File dummy = tempDir.resolve("dummy").toFile();
        List<File> pastFile = logger.getPastLogFilesFrom(dummy);

        assertThat(pastFile).isEmpty();
    }

    @Test
    void getPastLogFilesFrom() throws IOException {
        File pastFile = createFile(tempDir.resolve(PAST_LOG));
        File lastestFile = createFile(tempDir.resolve(LATEST_LOG));

        List<File> files = logger.getPastLogFilesFrom(lastestFile);

        files.forEach(file ->  assertThat(file.getName()).isEqualTo(pastFile.getName()));
    }

    @Test
    void compress() throws IOException {
        File file = createFile(tempDir.resolve(PAST_LOG));
        logger.compress(file);

        File compressedFile = tempDir.resolve(PAST_ZIP).toFile();
        assertThat(compressedFile.exists()).isTrue();
    }

    @Test
    void changeLogFile() throws IOException {
        File lastestFile = createFile(tempDir.resolve(LATEST_LOG));
        logger.changeLogFile(lastestFile);

        String[] fileNames = tempDir.toFile().list();
        String regex = "until_(\\d{6})_(\\d{2})h_(\\d{2})m_(\\d{2})s\\.log";
        Pattern pattern = Pattern.compile(regex);

        assertThat(fileNames).anyMatch(fileName -> {
            Matcher matcher = pattern.matcher(fileName);
            return matcher.matches();
        });
    }

    private File createFile(Path path) throws IOException {
        Files.createFile(path);
        return path.toFile();
    }
    @ParameterizedTest
    @ValueSource (ints = {1, 3, 5, 6})
    void writeLog(int repeat) throws IOException {
        String content = "Hello World!";

        for (int i = 0; i < repeat; i++) {
            logger.writeLog(content);
        }

        List<String> actual = Files.readAllLines(Paths.get(TEST_LOG_FILE));

        assertEquals(repeat, actual.size());
        String expected = "[" + currentDate + "] shell.LoggerTest.writeLog()             Hello World!";
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
        String actual = logger.formatLogContent("LoggerTest", "formatLogContent", "Hello World!");

        String expected = "[" + currentDate + "] LoggerTest.formatLogContent()           Hello World!";
        assertEquals(expected, actual);
    }

    private void setFileSize(File file, long bytes) throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
            randomAccessFile.setLength(bytes);
        }
    }
}
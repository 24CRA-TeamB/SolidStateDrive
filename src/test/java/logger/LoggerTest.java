package logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static logger.Logger.MAX_LOG_BYTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoggerTest {
    private static final String LATEST_LOG = "latest.log";
    private static final String PAST_LOG = "until_240101_00h_00m_00s.log";
    private static final String PAST_ZIP = "until_240101_00h_00m_00s.zip";

    Logger logger;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        logger = Logger.getInstance(tempDir.toString());
    }

    @Test
    void rollLogFile() throws IOException {
        Logger spyLogger = spy(Logger.class);

        doNothing().when(spyLogger).compress(any());
        doNothing().when(spyLogger).changeLogFile(any());

        File pastFile = createFile(tempDir.resolve(PAST_LOG));
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

    private void setFileSize(File file, long bytes) throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
            randomAccessFile.setLength(bytes);
        }
    }
}
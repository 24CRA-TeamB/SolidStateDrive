package logger;

import org.assertj.core.util.VisibleForTesting;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Logger {
    private static final HashMap<String, Logger> loggerMap = new HashMap<>();
    private static final String logFormat = "";
    static final long MAX_LOG_BYTES = 1000;
    private String logPath;

    Logger() {
        // for test
    }

    private Logger(String logPath) {
        this.logPath = logPath;
    }

    public static Logger getInstance(String filePath) {
        if (loggerMap.containsKey(filePath) == false) {
            Logger newLogger = new Logger(filePath);
            loggerMap.put(filePath, newLogger);
        }

        return loggerMap.get(filePath);
    }

    public void writeLog(String content) {
    }

    public File getLogFile() {
        return new File("");
    }

    public void rollLogFile(File logFile) {
        try {
            if (isLogSizeFull(logFile)) {
                File pastFile = getPastLogFileFrom(logFile);
                if (pastFile != null) {
                    compress(pastFile);
                }

                changeLogFile(logFile);
            }
        } catch (IOException e) {
            System.out.println("Failed to roll log file. " + e.getMessage());
        }
    }

    public String formatting(String content) {
        return content;
    }

    @VisibleForTesting
    boolean isLogSizeFull(File file) throws IOException {
        return Files.size(file.toPath()) > MAX_LOG_BYTES;
    }

    @VisibleForTesting
    File getPastLogFileFrom(File file) throws IOException {
        File logDir = file.getParentFile();

        for (File pastFile : logDir.listFiles()) {
            if (isCompressible(pastFile) && compareFileTime(file, pastFile) < 0) {
                return pastFile;
            }
        }

        return null;
    }

    @VisibleForTesting
    void compress(File file) throws IOException {
        Path logPath = file.toPath();

        String logName = logPath.getFileName().toString();
        String compressedName = logName.replace(".log", ".zip");

        Files.move(logPath, logPath.resolveSibling(compressedName));
    }

    @VisibleForTesting
    void changeLogFile(File file) throws IOException {
        Path logPath = file.toPath();

        DateFormat dateFormat = new SimpleDateFormat("'until'_yyMMdd_HH'h'_mm'm'_ss's'.'log'");
        String changedName = dateFormat.format(new Date());

        Files.move(logPath, logPath.resolveSibling(changedName));
    }

    private boolean isCompressible(File file) {
        return file.getName().endsWith(".log");
    }

    private int compareFileTime(File file, File pastFile) throws IOException {
        FileTime latestTime = Files.getLastModifiedTime(file.toPath());
        FileTime pastTime = Files.getLastModifiedTime(pastFile.toPath());

        return pastTime.compareTo(latestTime);
    }
}

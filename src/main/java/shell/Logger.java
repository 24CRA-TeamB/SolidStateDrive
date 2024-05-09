package shell;

import org.assertj.core.util.VisibleForTesting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Logger {
    private static final HashMap<String, Logger> loggerMap = new HashMap<>();
    public static final String FORMAT_TIMESTAMP = "yy.MM.dd HH:mm";
    public static final int METHOD_PADDING_LENGTH = 40;
    public static final long MAX_LOG_BYTES = 10000;
    private String logDir;
    private final String logFileName = "latest.log";

    @VisibleForTesting
    Logger() {
    }

    private Logger(String logDir){
        this.logDir = logDir;
    }

    public static Logger getInstance(String logDir) {
        if (loggerMap.containsKey(logDir) == false) {
            Logger newLogger = new Logger(logDir);
            loggerMap.put(logDir, newLogger);
        }

        makeDirectoryIfNotExist(logDir);

        return loggerMap.get(logDir);
    }

    public void writeLog(String content) {
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            String invokeClass = getInvokeClassName(stackTrace);
            String invokeMethod = getInvokeMethodName(stackTrace);
            String formattedContent = formatLogContent(invokeClass, invokeMethod, content);
            rollLogFile(getLogFile());
            appendLogFile(formattedContent);
            System.out.println(formattedContent);
        } catch (IOException e) {
            System.out.println("failed to write log");
        }
    }

    private void appendLogFile(String formattedContent) throws IOException {
        FileWriter fileWriter = new FileWriter(getLogFilePath(), true);
        fileWriter.write(formattedContent + "\n");
        fileWriter.close();
    }

    private String getInvokeClassName(StackTraceElement[] stackTraceElements) {
        return stackTraceElements[2].getClassName();
    }

    public String getInvokeMethodName(StackTraceElement[] stackTraceElements) {
        return stackTraceElements[2].getMethodName();
    }

    private String getLogFilePath() {
        return this.logDir + "/" + this.logFileName;
    }

    public void rollLogFile(File logFile) {
        try {
            if (isLogSizeFull(logFile)) {
                List<File> pastFiles = getPastLogFilesFrom(logFile);

                for (File file : pastFiles) {
                    compress(file);
                }

                changeLogFile(logFile);
            }
        } catch (IOException e) {
            System.out.println("Failed to roll log file. " + e.getMessage());
        }
    }


    public String formatLogContent(String className, String methodName, String content) {
        return "[" + getFormattedTime(new Date()) + "] " +
                getRightPaddedString(className + "." + methodName + "()", METHOD_PADDING_LENGTH) +
                content;
    }

    public String getFormattedTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy.MM.dd HH:mm");
        return simpleDateFormat.format(date);
    }

    private String getRightPaddedString(String content, int padLength) {
        return String.format("%1$-" + padLength + "s", content);
    }

    public File getLogFile() throws IOException {
        File logFile = new File(getLogFilePath());
        if (!logFile.exists()) {
            logFile.createNewFile();
        }
        return logFile;
    }

    @VisibleForTesting
    boolean isLogSizeFull(File file) throws IOException {
        return Files.size(file.toPath()) > MAX_LOG_BYTES;
    }

    @VisibleForTesting
    List<File> getPastLogFilesFrom(File file) throws IOException {
        File logDir = file.getParentFile();

        List<File> pastFiles = new ArrayList<>();

        for (File pastFile : logDir.listFiles()) {
            if (isCompressible(pastFile) && compareFileTime(file, pastFile) < 0) {
                pastFiles.add(pastFile);
            }
        }

        return pastFiles;
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

    private static void makeDirectoryIfNotExist(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}

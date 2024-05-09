package logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Logger {
    private static final HashMap<String, Logger> loggerMap = new HashMap<>();
    public static final String FORMAT_TIMESTAMP = "yy.MM.dd HH:mm";
    public static final int METHOD_PADDING_LENGTH = 30;
    private final String logDir;
    private final String logFileName = "latest.log";

    private Logger(String logDir){
        this.logDir = logDir;
    }

    public static Logger getInstance(String logDir) {
        if (loggerMap.containsKey(logDir) == false) {
            Logger newLogger = new Logger(logDir);
            loggerMap.put(logDir, newLogger);
        }

        return loggerMap.get(logDir);
    }

    public void writeLog(String content) {

    }

    private String getLogFilePath() {
        return this.logDir + "/" + this.logFileName;
    }

    public File rollingLogFile(File logFile) {
        return new File("");
    }

    public String formatLogContent(String method, String content) {
        return "[" + getFormattedTime(new Date()) + "] " +
                getRightPaddedString(method + "()", METHOD_PADDING_LENGTH) +
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
        System.out.println(getLogFilePath());
        File logFile = new File(getLogFilePath());
        if (!logFile.exists()) {
            logFile.createNewFile();
        }
        return logFile;
    }
}

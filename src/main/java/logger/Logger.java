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
    private final String logPath;

    private Logger(String logPath){
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

    public File getLogFile() throws IOException {
        File logFile = new File(this.logPath);
        if (!logFile.exists()) {
            logFile.createNewFile();
        }
        return logFile;
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
}

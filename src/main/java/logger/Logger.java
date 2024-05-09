package logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Logger {
    private static final HashMap<String, Logger> loggerMap = new HashMap<>();
    public static final String FORMAT_TIMESTAMP = "yy.MM.dd HH:mm";
    public static final int METHOD_PADDING_LENGTH = 30;
    private final String logPath;
    private File logFile;

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
        try {
            String invokeMethod = getInvokeMethodName(Thread.currentThread().getStackTrace());
            String formattedContent = formatLogContent(invokeMethod, content);
            getLogFile();
            appendLogFile(formattedContent);
            System.out.println(formattedContent);
        } catch (IOException e) {
            System.out.println("failed to write log");
        }
    }

    private void appendLogFile(String formattedContent) throws IOException {
        FileWriter fileWriter = new FileWriter(logPath, true);
        fileWriter.write(formattedContent + "\n");
        fileWriter.close();
    }

    public String getInvokeMethodName(StackTraceElement[] stackTraceElements) {
        return stackTraceElements[2].getMethodName();
    }

    public File getLogFile() throws IOException {
        logFile = new File(this.logPath);
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

package logger;

import java.io.File;
import java.util.HashMap;

public class Logger {
    private static final HashMap<String, Logger> loggerMap = new HashMap<>();
    private static final String logFormat = "";
    private final String logPath;

    private Logger(String logPath){
        this.logPath = logPath;
    }

    public Logger getInstance(String filePath) {
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

    public File rollingLogFile(File logFile) {
        return new File("");
    }

    public String formatting(String content) {
        return content;
    }
}

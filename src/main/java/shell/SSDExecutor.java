package shell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SSDExecutor {
    private final String jar;
    private final Logger logger = Logger.getInstance("./shell");

    public SSDExecutor(String jar) {
        this.jar = jar;
    }

    public void writeData(String lba, String data) {
        logger.writeLog("LBA=" + lba + ", DATA=" + data);
        runCommand("W", lba, data);
    }

    public void readData(String lba) {
        logger.writeLog("LBA=" + lba);
        runCommand("R", lba);
    }

    public void eraseData(String lba, String size) {
        logger.writeLog("LBA=" + lba + ", SIZE=" + size);
        runCommand("E", lba, size);
    }

    private void runCommand(String... args) {
        List<String> commands = new ArrayList<>(Arrays.asList("java", "-jar", jar));
        commands.addAll(Arrays.asList(args));

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(commands);
        String command = String.join(" ", processBuilder.command());

        try {
            Process process = processBuilder.start();
            process.waitFor();
            logger.writeLog("Success to run command " + command);
        } catch (Exception e) {
            logger.writeLog("Fail to run command " + command);
        }
    }
}

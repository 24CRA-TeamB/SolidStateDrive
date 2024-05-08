package shell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SSDExecutor {
    private final String jar;

    public SSDExecutor(String jar) {
        this.jar = jar;
    }

    public void writeData(String lba, String data) {
        runCommand("W", lba, data);
    }

    public void readData(String lba) {
        runCommand("R", lba);
    }

    public void eraseData(String lba, String size) {
        runCommand("E", lba, size);
    }

    private void runCommand(String... args) {
        List<String> commands = new ArrayList<>(Arrays.asList("java", "-jar", jar));
        commands.addAll(Arrays.asList(args));

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(commands);

        try {
            Process process = processBuilder.start();
            process.waitFor();
        } catch (Exception e) {
            String command = String.join(" ", processBuilder.command());
            System.out.println("Failed to run command " + command);
        }
    }
}

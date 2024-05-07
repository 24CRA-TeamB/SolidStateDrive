package shell;

public class SSDExecutor {
    private final String jar;

    public SSDExecutor(String jar) {
        this.jar = jar;
    }

    public void writeData(String lba, String data) {
        runCommand("ssd", "W", lba, data);
    }

    public void readData(String lba) {
        runCommand("ssd", "R", lba);
    }

    private void runCommand(String... args) {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", jar);
        processBuilder.command(args);

        try {
            Process process = processBuilder.start();
            process.waitFor();
        } catch (Exception e) {
            String command = String.join(" ", processBuilder.command());
            System.out.println("Failed to run command " + command);
        }
    }
}

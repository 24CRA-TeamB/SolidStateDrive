package shell;

import org.assertj.core.util.VisibleForTesting;
import ssd.DeviceDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestShell {
    // TODO
    // LBA 갯수는 DeviceDriver에서 가져오도록 고민
    public static final int NUMBER_OF_LBA = 100;
    private static final String RESULT_FILE = "result.txt";
    private DeviceDriver deviceDriver;

    public TestShell() {}

    public TestShell(DeviceDriver deviceDriver) {
        setDeviceDriver(deviceDriver);
    }

    public void setDeviceDriver(DeviceDriver deviceDriver) {
        this.deviceDriver = deviceDriver;
    }

    public void write(String lba, String data) {
        deviceDriver.writeData(lba, data);
    }

    public void fullwrite(String data) {
        for (int i = 0; i < NUMBER_OF_LBA; i++) {
            write(Integer.toString(i), data);
        }
    }

    public void read(String lba) {
        readFileAndPrint(deviceDriver, lba);
    }

    public void fullRead() {
        for (int i = 0; i < NUMBER_OF_LBA; i++) {
            readFileAndPrint(deviceDriver, String.valueOf(i));
        }
    }

    private void readFileAndPrint(DeviceDriver deviceDriver, String input) {
        deviceDriver.readData(input);

        print(RESULT_FILE);
    }

    public void exit() {
        System.out.println("program end");
        System.exit(0);
    }

    public void help() {

    }

    @VisibleForTesting
    void print(String file) {
        try {
            Path filePath = Paths.get(file);
            String result = new String(Files.readAllBytes(filePath));
            System.out.println(result);
        } catch (IOException e) {
            System.out.println("Failed to read result. " + e.getMessage());
        }
    }
}

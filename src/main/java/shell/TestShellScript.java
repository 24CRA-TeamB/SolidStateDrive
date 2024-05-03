package shell;

import org.assertj.core.util.VisibleForTesting;
import ssd.DeviceDriver;
import ssd.SSDInterface;
import java.util.Scanner;

public class TestShellScript {
    public static final int NUMBER_OF_LBA = 100;
    private static DeviceDriver deviceDriver;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();
        }
    }

    @VisibleForTesting
    static void write(String lba, String data) {
        deviceDriver.writeData(lba, data);
    }

    @VisibleForTesting
    static void fullwrite(String data) {
        for (int i = 0; i < NUMBER_OF_LBA; i++) {
            write(Integer.toString(i), data);
        }
    }

    @VisibleForTesting
    static void read() {

    }

    @VisibleForTesting
    static void fullread() {

    }

    @VisibleForTesting
    static void exit() {

    }

    @VisibleForTesting
    static void help() {

    }

    public static void setDeviceDriver(DeviceDriver device) {
        deviceDriver = device;
    }
}

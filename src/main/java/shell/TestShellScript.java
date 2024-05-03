package shell;

import org.assertj.core.util.Strings;
import org.assertj.core.util.VisibleForTesting;
import ssd.DeviceDriver;
import ssd.SamsungSSD;

import java.util.Scanner;

public class TestShellScript {
    public static final int NUMBER_OF_LBA = 100;
    private static DeviceDriver deviceDriver;


    private static final String RESULT_FILE = "result.txt";
    private static final String READ = "read";
    private static final String FULL_READ = "fullread";
    private static final String EXIT = "exit";
    private static boolean isExit = false;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (!isExit) {
            String input = scanner.nextLine();

            if (!Strings.isNullOrEmpty(input)) {
                continue;
            }

            String[] commands = input.split(" ");

            try {
                switch (commands[0]) {
                    case READ:
                        read(commands[1]);
                        break;
                    case FULL_READ:
                        fullRead();
                        break;
                    case EXIT:
                        exit();
                        break;
                    default:
                        System.out.println("Invalid commands.");
                }
            } catch (RuntimeException e) {
                System.out.println("An error occurred while running the command");
                System.out.println(e.getMessage());
            }
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
    static void read(String lba) {
        readFileAndPrint(deviceDriver, lba);
    }

    @VisibleForTesting
    static void fullRead() {
        for (int i = 0; i < NUMBER_OF_LBA; i++) {
            readFileAndPrint(deviceDriver, String.valueOf(i));
        }
    }

    private static void readFileAndPrint(DeviceDriver deviceDriver, String input) {
        deviceDriver.readData(input);

        DataReader.print(RESULT_FILE);
    }

    @VisibleForTesting
    static void exit() {
        isExit = true;
    }

    @VisibleForTesting
    static void help() {

    }

    public static void setDeviceDriver(DeviceDriver device) {
        deviceDriver = device;
    }
}

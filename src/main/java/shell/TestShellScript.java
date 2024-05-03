package shell;

import org.assertj.core.util.Strings;
import org.assertj.core.util.VisibleForTesting;
import ssd.DeviceDriver;
import ssd.SamsungSSD;

import java.util.Scanner;

public class TestShellScript {

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
                        write();
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
    static void write() {

    }

    @VisibleForTesting
    static void fullwrite() {

    }

    @VisibleForTesting
    static void read(int input) {
        DeviceDriver deviceDriver = new DeviceDriver(new SamsungSSD());

        readFileAndPrint(deviceDriver, input);
    }

    @VisibleForTesting
    static void fullRead() {
        DeviceDriver deviceDriver = new DeviceDriver(new SamsungSSD());

        for (int i = 0; i < 100; i++) {
            readFileAndPrint(deviceDriver, i);
        }
    }

    private static void readFileAndPrint(DeviceDriver deviceDriver, int input) {
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
}

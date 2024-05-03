package shell;

import org.assertj.core.util.Strings;
import ssd.DeviceDriver;
import ssd.SamsungSSD;

import java.util.Scanner;

public class TestShellScript {
    private static final String WRITE = "write";
    private static final String FULL_WRITE = "fullwrite";
    private static final String READ = "read";
    private static final String FULL_READ = "fullread";
    private static final String HELP = "help";
    private static final String EXIT = "exit";

    public static void main(String[] args) {
        TestShell testShell = new TestShell(new DeviceDriver(new SamsungSSD()));

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if (!Strings.isNullOrEmpty(input)) {
                continue;
            }

            String[] commands = input.split(" ");

            // TODO
            // invalid commands 에 대한 처리 구현 필요
            try {
                switch (commands[0]) {
                    case READ:
                        testShell.read(commands[1]);
                        break;
                    case FULL_READ:
                        testShell.fullRead();
                        break;
                    case WRITE:
                        testShell.write(commands[1], commands[2]);
                        break;
                    case FULL_WRITE:
                        testShell.fullwrite(commands[1]);
                        break;
                    case HELP:
                        testShell.help();
                        break;
                    case EXIT:
                        testShell.exit();
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

}

package shell;

import org.assertj.core.util.Strings;
import org.assertj.core.util.VisibleForTesting;

import java.util.Scanner;

public class TestShellScript {

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
                    System.out.println("Invalid commands");
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
    }

    @VisibleForTesting
    static void fullRead() {

    }

    @VisibleForTesting
    static void exit() {
        isExit = true;
    }

    @VisibleForTesting
    static void help() {

    }
}

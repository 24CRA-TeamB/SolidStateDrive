package shell;

import org.assertj.core.util.Strings;
import org.assertj.core.util.VisibleForTesting;
import java.util.Scanner;

public class TestShellScript {
    public static void main(String[] args) {
        if (Strings.isNullOrEmpty(args[0])) {
            System.out.println("ssd.jar is required.");
            System.exit(0);
        }

        String ssdJar = args[0];
        TestShell testShell = new TestShell(new SSDExecutor(ssdJar));

        while (true) {
            String[] userInputArray = getUserInput();

            String command = getCommandFromUserInput(userInputArray);
            String[] arguments = getArgumentsFromUserInput(userInputArray);

            testShell.run(command, arguments);
        }
    }

    @VisibleForTesting
    static String getCommandFromUserInput(String[] userInputArray) {
        String command = "";
        if (userInputArray.length >= 1) {
            command = userInputArray[0];
        }
        return command;
    }

    @VisibleForTesting
    static String[] getArgumentsFromUserInput(String[] userInputArray) {
        String[] arguments = new String[userInputArray.length - 1];
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = userInputArray[i + 1];
        }
        return arguments;
    }

    @VisibleForTesting
    static String[] getUserInput() {
        Scanner scanner = new Scanner(System.in);
        String input = "";
        while(true) {
            input = scanner.nextLine();
            if (!Strings.isNullOrEmpty(input)) {
                break;
            }
        }

        return input.split(" ");
    }
}

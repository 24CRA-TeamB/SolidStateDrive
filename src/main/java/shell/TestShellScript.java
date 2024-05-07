package shell;

import org.assertj.core.util.Strings;
import org.assertj.core.util.VisibleForTesting;
import ssd.DeviceDriver;
import ssd.SamsungSSD;

import java.util.Scanner;

public class TestShellScript {
    public static void main(String[] args) {
        TestShell testShell = new TestShell(new DeviceDriver(new SamsungSSD()));

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

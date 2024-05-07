package shell;

import org.assertj.core.util.Strings;
import org.assertj.core.util.VisibleForTesting;
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

    private static final int NUMBER_OF_ARGUMENTS_FOR_READ = 1;
    private static final int NUMBER_OF_ARGUMENTS_FOR_FULLREAD = 0;
    private static final int NUMBER_OF_ARGUMENTS_FOR_WRITE = 2;
    private static final int NUMBER_OF_ARGUMENTS_FOR_FULLWRITE = 1;
    private static final int NUMBER_OF_ARGUMENTS_FOR_HELP = 0;
    private static final int NUMBER_OF_ARGUMENTS_FOR_EXIT = 0;

    public static void main(String[] args) {
        TestShell testShell = new TestShell(new DeviceDriver(new SamsungSSD()));

        while (true) {
            String[] userInputArray = getUserInput();
            String command = getCommandFromUserInput(userInputArray);
            String[] arguments = getArgumentsFromUserInput(userInputArray);
            try {
                runTestShellWithCommand(testShell, command, arguments);
            } catch (RuntimeException e) {
                System.out.println("An error occurred while running the command");
                System.out.println(e.getMessage());
            }
        }
    }

    @VisibleForTesting
    static void runTestShellWithCommand(TestShell testShell, String command, String[] arguments) {
        switch (command) {
            case READ:
                if (NUMBER_OF_ARGUMENTS_FOR_READ == arguments.length) {
                    testShell.read(arguments[NUMBER_OF_ARGUMENTS_FOR_FULLREAD]);
                }
                break;
            case FULL_READ:
                if (NUMBER_OF_ARGUMENTS_FOR_FULLREAD == arguments.length) {
                    testShell.fullRead();
                }
                break;
            case WRITE:
                if (NUMBER_OF_ARGUMENTS_FOR_WRITE == arguments.length) {
                    testShell.write(arguments[NUMBER_OF_ARGUMENTS_FOR_FULLREAD], arguments[NUMBER_OF_ARGUMENTS_FOR_READ]);
                }
                break;
            case FULL_WRITE:
                if (NUMBER_OF_ARGUMENTS_FOR_FULLWRITE == arguments.length) {
                    testShell.fullwrite(arguments[NUMBER_OF_ARGUMENTS_FOR_FULLREAD]);
                }
                break;
            case HELP:
                if (NUMBER_OF_ARGUMENTS_FOR_HELP == arguments.length) {
                    testShell.help();
                }
                break;
            case EXIT:
                if (NUMBER_OF_ARGUMENTS_FOR_EXIT == arguments.length) {
                    testShell.exit();
                }
                break;
            default:
                System.out.println("Invalid commands.");
        }
    }

    @VisibleForTesting
    static String getCommandFromUserInput(String[] userInputArray) {
        String command = "";
        if (userInputArray.length >= NUMBER_OF_ARGUMENTS_FOR_READ) {
            command = userInputArray[NUMBER_OF_ARGUMENTS_FOR_FULLREAD];
        }
        return command;
    }

    @VisibleForTesting
    static String[] getArgumentsFromUserInput(String[] userInputArray) {
        String[] arguments = new String[userInputArray.length - NUMBER_OF_ARGUMENTS_FOR_READ];
        for (int i = NUMBER_OF_ARGUMENTS_FOR_FULLREAD; i < arguments.length; i++) {
            arguments[i] = userInputArray[i + NUMBER_OF_ARGUMENTS_FOR_READ];
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

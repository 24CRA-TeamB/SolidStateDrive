package shell;


import org.assertj.core.util.VisibleForTesting;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class TestShell {
    public static final String WRITE = "write";
    public static final String FULL_WRITE = "fullwrite";
    public static final String READ = "read";
    public static final String FULL_READ = "fullread";
    public static final String ERASE = "erase";
    public static final String ERASE_RANGE = "erase_range";
    public static final String HELP = "help";
    public static final String EXIT = "exit";
    public static final String TESTAPP1 = "testapp1";
    public static final String TESTAPP2 = "testapp2";
    public static final String INVALID_COMMAND = "invalid_command";
    private static final String[] EMPTY_ARGUMENTS = new String[]{};

    private static final int NUMBER_OF_ARGUMENTS_FOR_READ = 1;
    private static final int NUMBER_OF_ARGUMENTS_FOR_FULLREAD = 0;
    private static final int NUMBER_OF_ARGUMENTS_FOR_WRITE = 2;
    private static final int NUMBER_OF_ARGUMENTS_FOR_FULLWRITE = 1;
    private static final int NUMBER_OF_ARGUMENTS_FOR_ERASE = 2;
    private static final int NUMBER_OF_ARGUMENTS_FOR_ERASE_RANGE = 2;
    private static final int NUMBER_OF_ARGUMENTS_FOR_HELP = 0;
    private static final int NUMBER_OF_ARGUMENTS_FOR_EXIT = 0;
    private static final int NUMBER_OF_ARGUMENTS_FOR_TESTAPP1 = 0;
    private static final int NUMBER_OF_ARGUMENTS_FOR_TESTAPP2 = 0;

    public static final int NUMBER_OF_LBA = 100;
    private static final int MAX_ERASE_SIZE = 10;
    static final String RESULT_FILE = "result.txt";
    private final HashMap<String, Method> methodFactory = new HashMap<>();
    private SSDExecutor ssdExecutor;

    @VisibleForTesting
    TestShell() {
        // for test
        buildMethodFactory();
    }

    public TestShell(SSDExecutor ssdExecutor) {
        this.ssdExecutor = ssdExecutor;
        buildMethodFactory();
    }

    private void buildMethodFactory() {
        try {
            methodFactory.put(READ, this.getClass().getDeclaredMethod("readAndPrint", String[].class));
            methodFactory.put(FULL_READ, this.getClass().getDeclaredMethod("fullreadAndPrint", String[].class));
            methodFactory.put(WRITE, this.getClass().getDeclaredMethod("write", String[].class));
            methodFactory.put(FULL_WRITE, this.getClass().getDeclaredMethod("fullwrite", String[].class));
            methodFactory.put(HELP, this.getClass().getDeclaredMethod("help", String[].class));
            methodFactory.put(ERASE, this.getClass().getDeclaredMethod("erase", String[].class));
            methodFactory.put(ERASE_RANGE, this.getClass().getDeclaredMethod("erase_range", String[].class));
            methodFactory.put(EXIT, this.getClass().getDeclaredMethod("exit", String[].class));
            methodFactory.put(TESTAPP1, this.getClass().getDeclaredMethod("testapp1", String[].class));
            methodFactory.put(TESTAPP2, this.getClass().getDeclaredMethod("testapp2", String[].class));
            methodFactory.put(INVALID_COMMAND, this.getClass().getDeclaredMethod("invalidCommand", String[].class));
        } catch (NoSuchMethodException e) {
            System.out.println("NoSuchMethodExcetion " + e.getMessage());
            System.exit(-1);
        }
    }

    private Method getMethod(String command) {
        if (methodFactory.containsKey(command)) {
            return methodFactory.get(command);
        } else {
            return methodFactory.get(INVALID_COMMAND);
        }
    }

    public void run(String command, String[] arguments) {
        Method method = getMethod(command);
        try {
            method.invoke(arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(String[] arguments) {
        if (NUMBER_OF_ARGUMENTS_FOR_WRITE != arguments.length) {
            return;
        }

        ssdExecutor.writeData(arguments[0], arguments[1]);
    }

    public void fullwrite(String[] arguments) {
        if (NUMBER_OF_ARGUMENTS_FOR_FULLWRITE != arguments.length) {
            return;
        }

        for (int i = 0; i < NUMBER_OF_LBA; i++) {
            write(new String[]{Integer.toString(i), arguments[0]});
        }
    }

    public String read(String[] arguments) {
        if (NUMBER_OF_ARGUMENTS_FOR_READ != arguments.length) {
            return "";
        }

        ssdExecutor.readData(arguments[0]);
        return readResult(RESULT_FILE);
    }

    public void readAndPrint(String[] arguments) {
        if (NUMBER_OF_ARGUMENTS_FOR_READ != arguments.length) {
            return;
        }

        System.out.println(read(arguments));
    }

    public ArrayList<String> fullread(String[] arguments) {
        ArrayList<String> result = new ArrayList<>();
        if (NUMBER_OF_ARGUMENTS_FOR_FULLREAD != arguments.length) {
            return result;
        }

        for (int i = 0; i < NUMBER_OF_LBA; i++) {
            ssdExecutor.readData(Integer.toString(i));
            result.add(readResult(RESULT_FILE));
        }

        return result;
    }

    public void fullreadAndPrint(String[] arguments) {
        if (NUMBER_OF_ARGUMENTS_FOR_FULLREAD != arguments.length) {
            return;
        }

        fullread(arguments).forEach(System.out::println);
    }

    public void erase(String[] arguments) {
        if (NUMBER_OF_ARGUMENTS_FOR_ERASE != arguments.length) {
            return;
        }

        if (isNotNumberFormat(arguments[0]) || isNotNumberFormat(arguments[1])) {
            return;
        }

        int lba = Integer.parseInt(arguments[0]);
        int size = Integer.parseInt(arguments[1]);

        if (size == 0) {
            System.out.println("can not erase with size 0");
            return;
        }

        erazeSplit(size, lba);
    }

    public void erase_range(String[] arguments) {
        if (NUMBER_OF_ARGUMENTS_FOR_ERASE_RANGE != arguments.length) {
            return;
        }

        if (isNotNumberFormat(arguments[0]) || isNotNumberFormat(arguments[1])) {
            return;
        }

        int startLBA = Integer.parseInt(arguments[0]);
        int endLBA = Integer.parseInt(arguments[1]);

        erazeSplit(startLBA, endLBA - startLBA);
    }

    private void erazeSplit(int size, int lba) {
        while (size > 0) {
            int erazeSize = Math.min(size, MAX_ERASE_SIZE);

            if (lba + erazeSize > NUMBER_OF_LBA) {
                erazeSize = NUMBER_OF_LBA - lba;
            }

            ssdExecutor.eraseData(String.valueOf(lba), String.valueOf(erazeSize));
            size -= MAX_ERASE_SIZE;
            lba += MAX_ERASE_SIZE;
        }
    }

    private boolean isNotNumberFormat(String numberString) {
        try {
            Integer.parseInt(numberString);
            return false;
        } catch (NullPointerException | NumberFormatException e) {
            return true;
        }
    }

    public void exit(String[] arguments) {
        if (NUMBER_OF_ARGUMENTS_FOR_EXIT != arguments.length) {
            return;
        }

        System.out.println("program end");
        System.exit(0);
    }

    public void help(String[] arguments) {
        if (NUMBER_OF_ARGUMENTS_FOR_HELP != arguments.length) {
            return;
        }

        System.out.println("Usage of TestShell");
        System.out.println("write\t\twrite data at a LBA. ex) write [LBA] [Data]");
        System.out.println("fullwrite\twrite data at all of LBA. ex) write [Data]");
        System.out.println("read\t\tread data from a LBA. ex) read [LBA]");
        System.out.println("fullread\tread data from all of LBA. ex) fullread");
        System.out.println("help\t\tprint description of TestShell. ex) help");
        System.out.println("exit\t\tend TestShell. ex) exit");
    }

    public void testapp1(String[] arguments) {
        if (NUMBER_OF_ARGUMENTS_FOR_TESTAPP1 != arguments.length) {
            return;
        }

        fullwrite(new String[]{"0x12345678"});
        ArrayList<String> result = fullread(new String[]{});
        verifyTestApp1(result);
    }

    public void testapp2(String[] arguments) {
        if (NUMBER_OF_ARGUMENTS_FOR_TESTAPP2 != arguments.length) {
            return;
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 30; j++) {
                write(new String[]{Integer.toString(i), "0xAAAABBBB"});
            }
        }

        for (int i = 0; i < 5; i++) {
            write(new String[]{Integer.toString(i), "0x12345678"});
        }

        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            result.add(read(new String[]{Integer.toString(i)}));
        }

        verifyTestApp2(result);
    }

    private void verifyTestApp1(ArrayList<String> result) {
        if (result.size() != NUMBER_OF_LBA) {
            System.out.println("TestApp1 fail");
        }

        for (int i = 0; i < NUMBER_OF_LBA; i++) {
            String expected = i + " " + "0x12345678";
            if (expected.equals(result.get(i)) == false) {
                System.out.println("TestApp1 fail");
                return;
            }
        }

        System.out.println("TestApp1 success");
    }

    private void verifyTestApp2(ArrayList<String> result) {
        if (result.size() != 5) {
            System.out.println("TestApp2 fail");
        }

        for (int i = 0; i < 5; i++) {
            String expected = i + " " + "0x12345678";
            if (expected.equals(result.get(i)) == false) {
                System.out.println("TestApp2 fail");
                return;
            }
        }

        System.out.println("TestApp2 success");
    }

    public String readResult(String file) {
        try {
            Path filePath = Paths.get(file);
            return new String(Files.readAllBytes(filePath));
        } catch (IOException e) {
            System.out.println("Failed to read result. " + e.getMessage());
            return "";
        }
    }

    public void invalidCommand(String[] arguments) {
        System.out.println("Invalid commands.");
    }

    @VisibleForTesting
    void setSSDExecutor(SSDExecutor ssdExecutor) {
        this.ssdExecutor = ssdExecutor;
    }
}

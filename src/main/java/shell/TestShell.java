package shell;

import ssd.DeviceDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TestShell {
    public static final String WRITE = "write";
    public static final String FULL_WRITE = "fullwrite";
    public static final String READ = "read";
    public static final String FULL_READ = "fullread";
    public static final String HELP = "help";
    public static final String EXIT = "exit";
    public static final String TESTAPP1 = "testapp1";
    public static final String TESTAPP2 = "testapp2";
    private static final String[] EMPTY_ARGUMENTS = new String[] {};

    private static final int NUMBER_OF_ARGUMENTS_FOR_READ = 1;
    private static final int NUMBER_OF_ARGUMENTS_FOR_FULLREAD = 0;
    private static final int NUMBER_OF_ARGUMENTS_FOR_WRITE = 2;
    private static final int NUMBER_OF_ARGUMENTS_FOR_FULLWRITE = 1;
    private static final int NUMBER_OF_ARGUMENTS_FOR_HELP = 0;
    private static final int NUMBER_OF_ARGUMENTS_FOR_EXIT = 0;

    public static final int NUMBER_OF_LBA = 100;
    static final String RESULT_FILE = "result.txt";
    private DeviceDriver deviceDriver;

    public TestShell() {
    }

    public TestShell(DeviceDriver deviceDriver) {
        setDeviceDriver(deviceDriver);
    }

    public void setDeviceDriver(DeviceDriver deviceDriver) {
        this.deviceDriver = deviceDriver;
    }

    public DeviceDriver getDeviceDriver() {
        return this.deviceDriver;
    }

    public void run(String command, String[] arguments) {
        switch (command) {
            case READ:
                readAndPrint(arguments);
                break;
            case FULL_READ:
                fullreadAndPrint(arguments);
                break;
            case WRITE:
                write(arguments);
                break;
            case FULL_WRITE:
                fullwrite(arguments);
                break;
            case HELP:
                help(arguments);
                break;
            case EXIT:
                exit(arguments);
                break;
            case TESTAPP1:
                testapp1();
                break;
            case TESTAPP2:
                testapp2();
                break;
            default:
                System.out.println("Invalid commands.");
        }
    }

    public void write(String[] arguments) {
        if (NUMBER_OF_ARGUMENTS_FOR_WRITE != arguments.length) {
            return;
        }

        deviceDriver.writeData(arguments[0], arguments[1]);
    }

    public void fullwrite(String[] arguments) {
        if (NUMBER_OF_ARGUMENTS_FOR_FULLWRITE != arguments.length) {
            return;
        }

        for (int i = 0; i < NUMBER_OF_LBA; i++) {
            write(new String[] {Integer.toString(i), arguments[0]});
        }
    }

    public String read(String[] arguments) {
        if (NUMBER_OF_ARGUMENTS_FOR_READ != arguments.length) {
            return "";
        }

        deviceDriver.readData(arguments[0]);
        return readResult(RESULT_FILE);
    }

    public void readAndPrint(String[] arguments) {
        System.out.println(read(arguments));
    }

    public ArrayList<String> fullread(String[] arguments) {
        ArrayList<String> result = new ArrayList<>();
        if (NUMBER_OF_ARGUMENTS_FOR_FULLREAD != arguments.length) {
            return result;
        }

        for (int i = 0; i < NUMBER_OF_LBA; i++) {
            deviceDriver.readData(Integer.toString(i));
            result.add(readResult(RESULT_FILE));
        }

        return result;
    }

    public void fullreadAndPrint(String[] arguments) {
        fullread(arguments).forEach(System.out::println);
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

    public void testapp1() {
        fullwrite(new String[] {"0x12345678"});
        ArrayList<String> result = fullread(new String[] {});
        verifyTestApp1(result);
    }

    public void testapp2() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 30; j++) {
                write(new String[] {Integer.toString(i), "0xAAAABBBB"});
            }
        }

        for (int i = 0; i < 5; i++) {
            write(new String[] {Integer.toString(i), "0x12345678"});
        }

        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < 5; i++){
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
}

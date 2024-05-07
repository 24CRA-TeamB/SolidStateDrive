package shell;

import org.assertj.core.util.VisibleForTesting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestShell {
    // TODO
    // LBA 갯수는 DeviceDriver에서 가져오도록 고민
    public static final int NUMBER_OF_LBA = 100;
    static final String RESULT_FILE = "result.txt";

    private SSDExecutor ssdExecutor;

    public TestShell(SSDExecutor ssdExecutor) {
        this.ssdExecutor = ssdExecutor;
    }

    public void write(String lba, String data) {
        ssdExecutor.writeData(lba, data);
    }

    public void fullwrite(String data) {
        for (int i = 0; i < NUMBER_OF_LBA; i++) {
            write(Integer.toString(i), data);
        }
    }

    public void read(String lba) {
        readFileAndPrint(lba);
    }

    public void fullRead() {
        for (int i = 0; i < NUMBER_OF_LBA; i++) {
            readFileAndPrint(String.valueOf(i));
        }
    }

    private void readFileAndPrint(String input) {
        ssdExecutor.readData(input);

        print(RESULT_FILE);
    }

    public void exit() {
        System.out.println("program end");
        System.exit(0);
    }

    public void help() {
        System.out.println("Usage of TestShell");
        System.out.println("write\t\twrite data at a LBA. ex) write [LBA] [Data]");
        System.out.println("fullwrite\twrite data at all of LBA. ex) write [Data]");
        System.out.println("read\t\tread data from a LBA. ex) read [LBA]");
        System.out.println("fullread\tread data from all of LBA. ex) fullread");
        System.out.println("help\t\tprint description of TestShell. ex) help");
        System.out.println("exit\t\tend TestShell. ex) exit");
    }

    @VisibleForTesting
    void print(String file) {
        try {
            Path filePath = Paths.get(file);
            String result = new String(Files.readAllBytes(filePath));
            System.out.println(result);
        } catch (IOException e) {
            System.out.println("Failed to read result. " + e.getMessage());
        }
    }
}

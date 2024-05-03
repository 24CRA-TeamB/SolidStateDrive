package shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DataReader {
    private DataReader() {
    }

    public static void print(String file) {
        try {
            Path filePath = Paths.get(file);
            String result = new String(Files.readAllBytes(filePath));
            System.out.println(result);
        } catch (IOException e) {
            System.out.println("Failed to read result. " + e.getMessage());
        }
    }
}

package shell;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ssd.DeviceDriver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.*;
import static shell.TestShell.RESULT_FILE;

@ExtendWith(MockitoExtension.class)
class TestShellTest {
    public static final String HELP_DESCRIPTION = "Usage of TestShell\r\n"
            + "write\t\twrite data at a LBA. ex) write [LBA] [Data]\r\n"
            + "fullwrite\twrite data at all of LBA. ex) write [Data]\r\n"
            + "read\t\tread data from a LBA. ex) read [LBA]\r\n"
            + "fullread\tread data from all of LBA. ex) fullread\r\n"
            + "help\t\tprint description of TestShell. ex) help\r\n"
            + "exit\t\tend TestShell. ex) exit\r\n";
    @Spy
    TestShell spyTestShell;

    @Mock
    DeviceDriver mockDeviceDriver;

    private PrintStream originalOut;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        spyTestShell.setDeviceDriver(mockDeviceDriver);

        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void constructWithDeviceDriver() {
        TestShell testShell = new TestShell(mockDeviceDriver);

        assertEquals(mockDeviceDriver, testShell.getDeviceDriver());
    }

    @ParameterizedTest
    @MethodSource("getLBAandDataList")
    void writeNormally(String lba, String data) {
        spyTestShell.write(lba, data);
        verify(mockDeviceDriver, times(1)).writeData(lba, data);
    }

    @ParameterizedTest
    @MethodSource("getDataList")
    void fullwriteNormally(String data) {
        spyTestShell.fullwrite(data);
        verify(mockDeviceDriver, times(TestShell.NUMBER_OF_LBA)).writeData(anyString(), matches(data));
    }

    @Test
    void read() {
        String lba = "10";
        doNothing().when(spyTestShell).print(anyString());

        spyTestShell.read(lba);

        verify(mockDeviceDriver, times(1)).readData(lba);
    }

    @Test
    void fullRead() {
        doNothing().when(spyTestShell).print(anyString());

        spyTestShell.fullRead();

        verify(mockDeviceDriver, times(100)).readData(anyString());
    }

    @Test
    void printSuccess(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve(RESULT_FILE);
        Files.createFile(filePath);

        String input = "123";
        Files.write(filePath, input.getBytes());

        spyTestShell.print(filePath.toString());

        String actual = outputStream.toString().trim();
        assertThat(actual).isEqualTo(input);
    }

    @Test
    void printFail() {
        String file = "not_existed_file.txt";

        spyTestShell.print(file);

        String actual = outputStream.toString().trim();
        String expected = "Failed to read result. " + file;

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void exit() {
    }

    @Test
    void help() {


        spyTestShell.help();

        assertEquals(HELP_DESCRIPTION, outputStream.toString());

    }

    static Stream<Arguments> getLBAandDataList() {
        return Stream.of(
                Arguments.arguments("0", "0x00000000"),
                Arguments.arguments("3", "0xAAAABBBB"),
                Arguments.arguments("99", "0xFFFFFFFF")
        );
    }

    static Stream<Arguments> getDataList() {
        return Stream.of(
                Arguments.arguments("0x00000000"),
                Arguments.arguments("0xAAAABBBB"),
                Arguments.arguments("0xFFFFFFFF")
        );
    }
}
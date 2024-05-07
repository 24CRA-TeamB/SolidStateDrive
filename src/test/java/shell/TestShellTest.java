package shell;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private static final String NOT_EXISTED_FILE = "not_existed_file.txt";
    private static final String SSD_JAR = "ssd.jar";

    @Mock
    SSDExecutor mockSSDExecutor;

    TestShell testShell;

    private PrintStream originalOut;
    private ByteArrayOutputStream outputStream;

    TestShellTest() {
        mockSSDExecutor = new SSDExecutor(SSD_JAR);
    }

    @BeforeEach
    void setUp() {
        testShell = new TestShell(mockSSDExecutor);

        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @ParameterizedTest
    @MethodSource("getLBAandDataList")
    void writeNormally(String lba, String data) {
        doNothing().when(mockSSDExecutor).writeData(lba, data);

        testShell.write(lba, data);

        verify(mockSSDExecutor, times(1)).writeData(lba, data);
    }

    @ParameterizedTest
    @MethodSource("getDataList")
    void fullwriteNormally(String data) {
        doNothing().when(mockSSDExecutor).writeData(anyString(), matches(data));

        testShell.fullwrite(data);

        verify(mockSSDExecutor, times(TestShell.NUMBER_OF_LBA)).writeData(anyString(), matches(data));
    }

    @Test
    void read() {
        String lba = "10";
        doNothing().when(mockSSDExecutor).readData(lba);

        testShell.read(lba);

        verify(mockSSDExecutor, times(1)).readData(lba);
    }

    @Test
    void fullRead() {
        doNothing().when(mockSSDExecutor).readData(anyString());

        testShell.fullRead();

        verify(mockSSDExecutor, times(100)).readData(anyString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "aaa", "0xFF"})
    void printSuccess(String input, @TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve(RESULT_FILE);
        Files.createFile(filePath);

        Files.write(filePath, input.getBytes());

        testShell.print(filePath.toString());

        String actual = outputStream.toString().trim();
        assertThat(actual).isEqualTo(input);
    }

    @Test
    void printFail() {
        testShell.print(NOT_EXISTED_FILE);

        String actual = outputStream.toString().trim();
        String expected = "Failed to read result. " + NOT_EXISTED_FILE;

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void exit() {
    }

    @Test
    void help() {
        testShell.help();

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
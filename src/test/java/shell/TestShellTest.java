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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.mockito.stubbing.Stubber;
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
    private static final String NOT_EXISTED_FILE = "not_existed_file.txt";

    @Spy
    TestShell spyTestShell;

    @Mock
    DeviceDriver mockDeviceDriver;

    private PrintStream originalOut;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        lenient().doNothing().when(mockDeviceDriver).writeData(anyString(), anyString());
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
        spyTestShell.write(new String[] {lba, data});
        verify(mockDeviceDriver, times(1)).writeData(lba, data);
    }

    @ParameterizedTest
    @MethodSource("getDataList")
    void fullwriteNormally(String data) {
        spyTestShell.fullwrite(new String[]{data});
        verify(mockDeviceDriver, times(TestShell.NUMBER_OF_LBA)).writeData(anyString(), matches(data));
    }

    @Test
    void read() {
        String[] arguments = new String[] {"10"};

        spyTestShell.read(arguments);

        verify(mockDeviceDriver, times(1)).readData(arguments[0]);
    }

    @Test
    void fullRead() {
        String[] arguments = new String[] {};

        spyTestShell.fullread(arguments);

        verify(mockDeviceDriver, times(100)).readData(anyString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "aaa", "0xFF"})
    void printSuccess(String input, @TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve(RESULT_FILE);
        Files.createFile(filePath);

        Files.write(filePath, input.getBytes());

        String actual = spyTestShell.readResult(filePath.toString());
        assertThat(actual).isEqualTo(input);
    }

    @Test
    void printFail() {
        spyTestShell.readResult(NOT_EXISTED_FILE);

        String actual = outputStream.toString().trim();
        String expected = "Failed to read result. " + NOT_EXISTED_FILE;

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void exit() {
    }

    @Test
    void help() {
        String[] arguments = new String[]{};

        spyTestShell.help(arguments);

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

    @Test
    void testapp1() {
        doNothing().when(spyTestShell).fullwrite(any());
        stubReadResult("0x12345678", 0, 100);

        spyTestShell.testapp1();

        verify(spyTestShell, times(1)).fullwrite(any());
        verify(spyTestShell, times(1)).fullread(any());

        assertEquals("TestApp1 success\r\n", outputStream.toString());
    }

    @Test
    void testapp1_fail() {
        doNothing().when(spyTestShell).fullwrite(any());
        stubReadResult("0x87654321", 0, 100);

        spyTestShell.testapp1();

        verify(spyTestShell, times(1)).fullwrite(any());
        verify(spyTestShell, times(1)).fullread(any());

        assertEquals("TestApp1 fail\r\n", outputStream.toString());
    }

    @Test
    void testapp2() {
        stubReadResult("0x12345678", 0, 5);

        spyTestShell.testapp2();

        verify(mockDeviceDriver, times(150)).writeData(anyString(), matches("0xAAAABBBB"));
        verify(mockDeviceDriver, times(5)).writeData(anyString(), matches("0x12345678"));

        assertEquals("TestApp2 success\r\n", outputStream.toString());
    }

    @Test
    void testapp2_fail() {
        stubReadResult("0x87654321", 0, 5);

        spyTestShell.testapp2();

        verify(mockDeviceDriver, times(150)).writeData(anyString(), matches("0xAAAABBBB"));
        verify(mockDeviceDriver, times(5)).writeData(anyString(), matches("0x12345678"));

        assertEquals("TestApp2 fail\r\n", outputStream.toString());
    }

    private void stubReadResult(String data, int from, int to) {
        Stubber stubber = doReturn(from + " " + data);
        for (int i = from + 1; i < to; i++) {
            stubber = stubber.doReturn(i + " " + data);
        }
        stubber.when(spyTestShell).readResult(anyString());
    }
}
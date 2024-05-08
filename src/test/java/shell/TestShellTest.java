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
import org.mockito.stubbing.Stubber;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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

    @Spy
    TestShell testShell;

    private PrintStream originalOut;
    private ByteArrayOutputStream outputStream;

    TestShellTest() {
        mockSSDExecutor = new SSDExecutor(SSD_JAR);
        testShell = new TestShell();
    }

    @BeforeEach
    void setUp() {
        testShell.setSSDExecutor(mockSSDExecutor);

        lenient().doNothing().when(mockSSDExecutor).writeData(anyString(), anyString());

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

        testShell.write(new String[]{lba, data});

        verify(mockSSDExecutor, times(1)).writeData(lba, data);
    }

    @ParameterizedTest
    @MethodSource("getDataList")
    void fullwriteNormally(String data) {
        doNothing().when(mockSSDExecutor).writeData(anyString(), matches(data));

        testShell.fullwrite(new String[]{data});

        verify(mockSSDExecutor, times(TestShell.NUMBER_OF_LBA)).writeData(anyString(), matches(data));
    }

    @Test
    void read() {
        String[] arguments = new String[]{"10"};

        testShell.read(arguments);

        verify(mockSSDExecutor, times(1)).readData(arguments[0]);
    }

    @Test
    void fullRead() {
        String[] arguments = new String[]{};

        testShell.fullread(arguments);

        verify(mockSSDExecutor, times(100)).readData(anyString());
    }

    @Test
    void eraseRange_InvalidArguments() {
        ArrayList<String[]> invalidArugments = new ArrayList<>();
        invalidArugments.add(new String[]{});
        invalidArugments.add(new String[] {"1", "2", "3"});
        invalidArugments.add(new String[] {"aa", "bb"});
        invalidArugments.add(new String[] {"aa", "2"});
        invalidArugments.add(new String[] {"1", "bb"});

        invalidArugments.forEach(arguments -> testShell.erase_range(arguments));

        verify(testShell, times(0)).erase(any());
    }

    @ParameterizedTest
    @MethodSource("getValidSingleEraseRangeArguments")
    void eraseRange_withinEraseCapacity(String[] arguments) {
        testShell.erase_range(arguments);

        verify(testShell, times(1)).erase(any());
    }

    static Stream<Arguments> getValidSingleEraseRangeArguments() {
        return Stream.of(
                Arguments.arguments((Object) new String[] {"0", "9"}),
                Arguments.arguments((Object) new String[] {"11", "12"}),
                Arguments.arguments((Object) new String[] {"0", "10"}),
                Arguments.arguments((Object) new String[] {"25", "34"})
        );
    }

    @ParameterizedTest
    @MethodSource("getValidMultipleEraseRangeArguments")
    void eraseRange_withinEraseCapacity(String[] arguments, int expectedTimes) {
        testShell.erase_range(arguments);

        verify(testShell, times(expectedTimes)).erase(any());
    }

    static Stream<Arguments> getValidMultipleEraseRangeArguments() {
        return Stream.of(
                Arguments.arguments(new String[] {"0", "99"}, 10),
                Arguments.arguments(new String[] {"11", "50"}, 4),
                Arguments.arguments(new String[] {"11", "52"}, 5),
                Arguments.arguments(new String[] {"43", "27"}, 0),
                Arguments.arguments(new String[] {"78", "94"}, 2),
                Arguments.arguments(new String[] {"78", "190"}, 3),
                Arguments.arguments(new String[] {"-100", "100"}, 10)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "aaa", "0xFF"})
    void printSuccess(String input, @TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve(RESULT_FILE);
        Files.createFile(filePath);

        Files.write(filePath, input.getBytes());

        String actual = testShell.readResult(filePath.toString());
        assertThat(actual).isEqualTo(input);
    }

    @Test
    void printFail() {
        testShell.readResult(NOT_EXISTED_FILE);

        String actual = outputStream.toString().trim();
        String expected = "Failed to read result. " + NOT_EXISTED_FILE;

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void eraseSizeZero() {
        testShell.erase(new String[]{"1", "0"});

        String expected = "can not erase with size 0";
        String actual = outputStream.toString().trim();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void eraseOnce() {
        doNothing().when(mockSSDExecutor).eraseData(any(), any());

        testShell.erase(new String[]{"1", "5"});

        verify(mockSSDExecutor, times(1)).eraseData("1", "5");
    }

    @Test
    void eraseMultiple() {
        doNothing().when(mockSSDExecutor).eraseData(any(), any());

        testShell.erase(new String[]{"1", "23"});

        verify(mockSSDExecutor, times(1)).eraseData("1", "10");
        verify(mockSSDExecutor, times(1)).eraseData("11", "10");
        verify(mockSSDExecutor, times(1)).eraseData("21", "3");
    }

    @Test
    void eraseOverMaxLba() {
        doNothing().when(mockSSDExecutor).eraseData(any(), any());

        testShell.erase(new String[]{"85", "20"});

        verify(mockSSDExecutor, times(1)).eraseData("85", "10");
        verify(mockSSDExecutor, times(1)).eraseData("95", "5");
    }

    @Test
    void exit() {
    }

    @Test
    void help() {
        String[] arguments = new String[]{};

        testShell.help(arguments);

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
        doNothing().when(testShell).fullwrite(any());
        stubReadResult("0x12345678", 0, 100);

        testShell.testapp1(new String[]{});

        verify(testShell, times(1)).fullwrite(any());
        verify(testShell, times(1)).fullread(any());

        assertEquals("TestApp1 success\r\n", outputStream.toString());
    }

    @Test
    void testapp1_fail() {
        doNothing().when(testShell).fullwrite(any());

        stubReadResult("0x87654321", 0, 100);

        testShell.testapp1(new String[]{});

        verify(testShell, times(1)).fullwrite(any());
        verify(testShell, times(1)).fullread(any());

        assertEquals("TestApp1 fail\r\n", outputStream.toString());
    }

    @Test
    void testapp2() {
        stubReadResult("0x12345678", 0, 5);

        testShell.testapp2(new String[]{});

        verify(mockSSDExecutor, times(150)).writeData(anyString(), matches("0xAAAABBBB"));
        verify(mockSSDExecutor, times(5)).writeData(anyString(), matches("0x12345678"));

        assertEquals("TestApp2 success\r\n", outputStream.toString());
    }

    @Test
    void testapp2_fail() {
        stubReadResult("0x87654321", 0, 5);

        testShell.testapp2(new String[]{});

        verify(mockSSDExecutor, times(150)).writeData(anyString(), matches("0xAAAABBBB"));
        verify(mockSSDExecutor, times(5)).writeData(anyString(), matches("0x12345678"));

        assertEquals("TestApp2 fail\r\n", outputStream.toString());
    }

    private void stubReadResult(String data, int from, int to) {
        Stubber stubber = doReturn(from + " " + data);
        for (int i = from + 1; i < to; i++) {
            stubber = stubber.doReturn(i + " " + data);
        }
        stubber.when(testShell).readResult(anyString());
    }
}
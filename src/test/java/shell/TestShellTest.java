package shell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ssd.DeviceDriver;

import java.io.ByteArrayOutputStream;
import java.util.stream.Stream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.*;

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

    @BeforeEach
    void setUp() {
        spyTestShell.setDeviceDriver(mockDeviceDriver);
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
        spyTestShell.fullRead();

        verify(mockDeviceDriver, times(100)).readData(anyString());
    }

    @Test
    void exit() {
    }

    @Test
    void help() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        spyTestShell.help();

        assertEquals(HELP_DESCRIPTION, outputStream.toString());
        System.setOut(originalOut);
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
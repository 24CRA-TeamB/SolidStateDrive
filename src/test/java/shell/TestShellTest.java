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

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestShellTest {
//    @Spy
    TestShell testShell;

    @Mock
    DeviceDriver mockDeviceDriver;

    @BeforeEach
    void setUp() {
        testShell = new TestShell(mockDeviceDriver);
    }

    @ParameterizedTest
    @MethodSource("getLBAandDataList")
    void writeNormally(String lba, String data) {
        testShell.write(lba, data);
        verify(mockDeviceDriver, times(1)).writeData(lba, data);
    }

    @ParameterizedTest
    @MethodSource("getDataList")
    void fullwriteNormally(String data) {
        testShell.fullwrite(data);
        verify(mockDeviceDriver, times(TestShell.NUMBER_OF_LBA)).writeData(anyString(), matches(data));
    }

    @Test
    void read() {
        String lba = "10";
        // doNothing().when(testShell).print(anyString());
        testShell.read(lba);

        verify(mockDeviceDriver, times(1)).readData(lba);
    }

    @Test
    void fullRead() {
        testShell.fullRead();

        verify(mockDeviceDriver, times(100)).readData(anyString());
    }

    @Test
    void exit() {
    }

    @Test
    void help() {
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
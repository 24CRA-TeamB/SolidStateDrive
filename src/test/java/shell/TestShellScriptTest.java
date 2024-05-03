package shell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ssd.DeviceDriver;
import ssd.SSDInterface;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TestShellScriptTest {

    @Mock
    DeviceDriver mockDeviceDriver;

    @BeforeEach
    void setUp() {
        TestShellScript.setDeviceDriver(mockDeviceDriver);
    }

    @ParameterizedTest
    @MethodSource("getLBAandDataList")
    void writeNormally(String lba, String data) {
        TestShellScript.write(lba, data);
        verify(mockDeviceDriver, times(1)).writeData(lba, data);
    }

    @ParameterizedTest
    @MethodSource("getDataList")
    void fullwriteNormally(String data) {
        TestShellScript.fullwrite(data);
        verify(mockDeviceDriver, times(TestShellScript.NUMBER_OF_LBA)).writeData(anyString(), matches(data));
    }

    @Test
    void read() {
        String lba = "10";

        TestShellScript.read(lba);

        verify(mockDeviceDriver, times(1)).readData(lba);
    }

    @Test
    void fullRead() {
        TestShellScript.fullRead();

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
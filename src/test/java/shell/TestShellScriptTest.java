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

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TestShellScriptTest {

    public static final int NUMBER_OF_LBA = 100;
    @Mock
    DeviceDriver mockDeviceDriver;

    @BeforeEach
    void setUp() {
        TestShellScript.setDeviceDriver(mockDeviceDriver);
    }

    @Test
    void main() {
    }

    @ParameterizedTest
    @MethodSource("getLBAandDataList")
    void write_normally(String lba, String data) {
        TestShellScript.write(lba, data);
        verify(mockDeviceDriver, times(1)).writeData(lba, data);
    }

    @ParameterizedTest
    @MethodSource("getDataList")
    void fullwrite_normally(String data) {
        TestShellScript.fullwrite(data);
        verify(mockDeviceDriver, times(NUMBER_OF_LBA)).writeData(anyString(), matches(data));
    }

    @Test
    void read() {
    }

    @Test
    void fullread() {
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
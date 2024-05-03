package shell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ssd.DeviceDriver;
import ssd.SSDInterface;
import ssd.SamsungSSD;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TestShellScriptTest {

    @Mock
    private DeviceDriver deviceDriver;

    @BeforeEach
    void setUp() {
    }

    @Test
    void write() {
    }

    @Test
    void fullwrite() {
    }

    @Test
    void read() {
        int input = 10;

        TestShellScript.read(input);

        verify(deviceDriver, times(1)).readData(input);
    }

    @Test
    void fullRead() {
        TestShellScript.fullRead();

        verify(deviceDriver, times(100)).readData(anyInt());
    }

    @Test
    void exit() {
    }

    @Test
    void help() {
    }
}
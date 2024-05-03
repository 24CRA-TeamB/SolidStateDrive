package shell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ssd.DeviceDriver;
import ssd.SamsungSSD;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TestShellScriptTest {

    @Mock
    private SamsungSSD ssdInterface;

    private DeviceDriver deviceDriver;

    @BeforeEach
    void setUp() {
        deviceDriver = new DeviceDriver(ssdInterface);
    }

    @Test
    void main() {
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

        verify(ssdInterface, times(1)).read(input);
    }

    @Test
    void fullRead() {
        TestShellScript.fullRead();

        verify(ssdInterface, times(100)).read(anyInt());
    }

    @Test
    void exit() {
    }

    @Test
    void help() {
    }
}
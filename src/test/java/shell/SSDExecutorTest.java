package shell;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SSDExecutorTest {
    @Spy
    SSDExecutor ssdExecutor;

    public SSDExecutorTest() {
        ssdExecutor = new SSDExecutor("ssd.jar");
    }

    @ParameterizedTest
    @CsvSource({"0,0x00000000", "1,0x11111111", "99,0x99999999"})
    void writeData(String lba, String data) {
        doNothing().when(ssdExecutor).runCommand("W", lba, data);

        ssdExecutor.writeData(lba, data);

        verify(ssdExecutor, times(1)).runCommand("W", lba, data);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "1", "99"})
    void readData(String lba) {
        doNothing().when(ssdExecutor).runCommand("R", lba);

        ssdExecutor.readData(lba);

        verify(ssdExecutor, times(1)).runCommand("R", lba);
    }

    @ParameterizedTest
    @CsvSource({"0,1", "1,2", "99,10"})
    void eraseData(String lba, String size) {
        doNothing().when(ssdExecutor).runCommand("E", lba, size);

        ssdExecutor.eraseData(lba, size);

        verify(ssdExecutor, times(1)).runCommand("E", lba, size);
    }
}
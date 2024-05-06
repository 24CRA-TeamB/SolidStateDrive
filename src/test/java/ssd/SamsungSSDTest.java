package ssd;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.junit.jupiter.api.Assertions.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SamsungSSDTest {
    public static final String READ_DATA_TARGET_FILE = "src/main/resources/nand.txt";

    @Mock
    SSDInterface ssdInterface;

    DeviceDriver deviceDriver;

    SSDInterface ssd;

    @BeforeEach
    void setUp() {
        deviceDriver = new DeviceDriver(ssdInterface);
        ssd = new SamsungSSD();
    }

    @Test
    void readInvalidArgumentMinusLBATest(){
        deviceDriver.readData("-1");
        verify(ssdInterface, times(0)).read("-1");
    }

    @Test
    void readInvalidArgumentOverMaxLBATest(){
        deviceDriver.readData("100");
        verify(ssdInterface, times(0)).read("100");
    }

    @Test
    void readLBAData(){
        deviceDriver.readData("3");
        verify(ssdInterface, times(1)).read("3");
    }

    @Test
    @DisplayName("SamsungSSD 객체가 정상적으로 생성된다")
    void createSamsungSSD(){
        assertThat(ssd).isNotNull();
    }

    @Test
    @DisplayName("data 에 16진수 값이 넘는 값이 전달되면 RuntimeException 을 던진다")
    void writeThrowRuntimeExceptionWhenDataValueExceedInteger(){
        try {
            // when
            ssd.write("3", "0x1298CDXF");
            fail();
        } catch(RuntimeException e) {
            // then
            assertThat(e).isInstanceOf(RuntimeException.class);
        }
    }

    @Test
    @DisplayName("lba 에 0보다 작은 값이 전달되는 경우 RuntimeException 을 던진다")
    void writeThrowRuntimeExceptionWhenLbaValueExceedInteger(){
        try {
            // when
            ssd.write("-1", "0x12345678");
            fail();
        } catch(RuntimeException e) {
            // then
            assertThat(e).isInstanceOf(RuntimeException.class);
        }
    }

    @Test
    @DisplayName("data 값에 prefix 0x 값이 아닌 경우 RuntimeException")
    void writeThrowRuntimeExceptionWhenPrefixValueIsNot0x(){
        try {
            ssd.write("1", "0012345678");
        } catch(RuntimeException e) {
            assertThat(e).isInstanceOf(RuntimeException.class);
        }
    }

    @Test
    @DisplayName("write가 동작했을 때 nand.txt 파일이 없다면 생성한다")
    void givenNandTxtDoesNotExist_whenWrite_thenCreateNandTxtFile(){
        // given
        File file = new File(READ_DATA_TARGET_FILE);
        if(file.exists()){
            file.delete();
        }

        // when
        ssd.write("1", "0x12345678");

        // then
        File nandTxt = new File(READ_DATA_TARGET_FILE);
        assertThat(nandTxt.exists()).isTrue();
    }
}
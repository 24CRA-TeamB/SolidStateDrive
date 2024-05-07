package ssd;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SamsungSSDTest {

    @Mock
    SSDInterface ssdInterfaceMock;

    DeviceDriver deviceDriverMock;

    DeviceDriver deviceDriver;

    @BeforeEach
    void setUp() {
        deviceDriverMock = new DeviceDriver(ssdInterfaceMock);
        deviceDriver = new DeviceDriver(new SamsungSSD());
    }

    @Test
    void readInvalidArgumentMinusLBATest(){
        deviceDriverMock.readData("-1");
        verify(ssdInterfaceMock, times(0)).read("-1");
    }

    @Test
    void readInvalidArgumentOverMaxLBATest(){
        deviceDriverMock.readData("100");
        verify(ssdInterfaceMock, times(0)).read("100");
    }

    @Test
    void readInterfaceLbaData(){
        deviceDriverMock.readData("14");
        verify(ssdInterfaceMock, times(1)).read("14");
    }

    @Test
    void readLbaData(){
        deviceDriver.readData("4");
    }

    @Nested
    class SamsungSSDWrite {
        SamsungSSD ssd;

        @BeforeEach
        void setUp() {
            ssd = new SamsungSSD();
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
    }
}
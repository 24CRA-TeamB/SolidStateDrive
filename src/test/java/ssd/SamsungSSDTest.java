package ssd;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SamsungSSDTest {

    @Mock
    SSDInterface ssdInterface;

    DeviceDriver deviceDriver;

    @BeforeEach
    void setUp() {
        deviceDriver = new DeviceDriver(ssdInterface);
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
        @DisplayName("3번 LBA 영역에 값 0x1298CDEF를 저장한다")
        void writeThrowRuntimeExceptionWhenDataValueExceedInteger(){
            // given


            // when
            ssd.write("3", "0x1298CDEF");

            // then
        }

        @Test
        @DisplayName("3번 LBA 영역에 값 0x1298CDEF를 저장한다")
        void writeLogicBlockAddress(){
            // given


            // when
            ssd.write("3", "0x1298CDEF");

            // then
        }
    }
}
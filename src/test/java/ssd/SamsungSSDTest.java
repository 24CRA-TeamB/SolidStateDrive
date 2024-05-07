package ssd;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
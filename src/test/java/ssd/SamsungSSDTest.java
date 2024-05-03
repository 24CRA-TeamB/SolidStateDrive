package ssd;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SamsungSSDTest {

    private SSDInterface ssdInterface;

    @BeforeEach
    void setUp() {
        this.ssdInterface = new SamsungSSD();
    }

    @Test
    void readInvalidArgumentTest(){
        assertThrows(InvalidLBAExcpetion.class, ()->{
           ssdInterface.read("-1");
        });

        assertThrows(InvalidLBAExcpetion.class, ()->{
            ssdInterface.read("100");
        });
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
            ssd.write(3, 0x1298CDEF);

            // then
        }

        @Test
        @DisplayName("3번 LBA 영역에 값 0x1298CDEF를 저장한다")
        void writeLogicBlockAddress(){
            // given


            // when
            ssd.write(3, 0x1298CDEF);

            // then
        }
    }
}
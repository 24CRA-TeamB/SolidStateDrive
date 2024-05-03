package ssd;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class SamsungSSDTest {
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
    }
}
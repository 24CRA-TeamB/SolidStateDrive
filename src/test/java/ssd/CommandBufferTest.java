package ssd;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CommandBufferTest {
    @Test
    @DisplayName("테스트 파일에 적힌 문자열을 읽어서 6개 Command 객체를 만든다")
    void givenSampleBufferFile_whenCheckCommandSize_thenSizeIs6(){
        // given
        CommandFactory commandFactory = CommandFactory.getInstance();
        CommandBuffer commandBuffer = new CommandBuffer(commandFactory);

        // when
        int size = commandBuffer.getSize();

        // then
        assertThat(size).isEqualTo(6);
    }
}
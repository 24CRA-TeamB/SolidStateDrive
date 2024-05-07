package ssd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SSDTest {

    public static final String NAND_TXT_PATH = "..\\nand.txt";
    public static final int WRITE_ARGUMENT_NUM = 3;
    public static final int READ_ARGUMENT_NUM = 2;
    public static final int MAX_LBA = 99;
    public static final int MIN_LBA = 0;
    String[] writeCommand = new String[WRITE_ARGUMENT_NUM];
    String[] readCommand = new String[READ_ARGUMENT_NUM];

    @Mock
    SSDInterface ssdInterface;

    SSD ssd;

    @BeforeEach
    void setUp() {
        ssd = new SSD(ssdInterface);
        ssd.createNandFile();
    }

    @Test
    @DisplayName("doCommand 명령어를 수행했을 때 read 명령어가 반드시 1회 수행된다 ")
    void doCommand() {
        // given
        setReadCommand("R", "1");

        // when
        ssd.doCommand(readCommand);

        // then
        verify(ssdInterface, times(1)).read("1");
    }

    @Test
    @DisplayName("lba 에 유효하지 않은 데이터 X가 전달될 때 유효성을 검증한다")
    void isImpossibleToParseToInt() {
        // given


        // when
        setWriteCommand("W", "1", "0x12345678");

        // then
        assertEquals(true, ssd.isImpossibleToParseToInt(writeCommand[1]));
    }

    @Test
    @DisplayName("Logic Block Address 값의 최소값과 최대값을 검증한다")
    void isInvalidLBA() {
        assertEquals(true, ssd.isInvalidLBA(MIN_LBA-1));
        assertEquals(true, ssd.isInvalidLBA(MAX_LBA+1));
    }

    @Test
    @DisplayName("File이 존재하지 않을 때, false 값을 반환한다")
    void doesNandFileExist() {
        assertEquals(false, new File(NAND_TXT_PATH).exists());
    }

    @Test
    @DisplayName("jar 파일로 W 명령어가 전달되었을 때, 드라이버 write 명령어가 1회 수행된다")
    void givenWriteCommand_whenWriteCommand_thenVerifyCallOnce(){
        // given
        setWriteCommand("W", "1", "0x12345678");

        // when
        ssd.doCommand(writeCommand);

        // then
        verify(ssdInterface, times(1)).write("1", "0x12345678");
    }

    @Test
    @DisplayName("lba 값으로 01이 전달될 때 false를 반환한다")
    void isImpossibleToParseToIntWith01(){
        // given
        setReadCommand("R", "01");

        // when
        boolean success = ssd.isImpossibleToParseToInt(readCommand[1]);

        // then
        assertFalse(success);
    }

    private void setWriteCommand(String writeCode, String lba, String data){
        writeCommand[0] = writeCode;
        writeCommand[1] = lba;
        writeCommand[2] = data;
    }

    private void setReadCommand(String writeCode, String lba){
        readCommand[1] = writeCode;
        readCommand[2] = lba;
    }
}
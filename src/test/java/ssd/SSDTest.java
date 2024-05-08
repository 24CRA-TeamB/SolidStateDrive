package ssd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SSDTest {

    public static final String NAND_TXT_PATH = "./nand.txt";
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
    }

    @Test
    @DisplayName("commandCode 값이 W 또는 R 아닌 값이 전달될 때, true 반환한다")
    void givenWorRCommandCode_whenCheckInvalidCommand_returnTrue(){
        // given
        setReadCommand("S", "15");

        // when
        boolean isInvalid = ssd.isInvalidCommand(readCommand);

        // then
        assertTrue(isInvalid);
    }

    @Test
    @DisplayName("parseInt 할 수 없는 lba 값이 전달되는 경우 true 반환한다")
    void givenInvalidLba_whenCheckInvalidCommand_returnTrue(){
        // given
        setReadCommand("R", "X");

        // when
        boolean isInvalid = ssd.isInvalidCommand(readCommand);

        // then
        assertTrue(isInvalid);
    }

    @Test
    @DisplayName("음수 LBA 가 전달되는 경우 true 반환한다")
    void givenOutOfRangeLbaMinus_whenCheckValidCommand_returnTrue(){
        // given
        setReadCommand("R", "-1");

        // when
        boolean isInvalid = ssd.isInvalidCommand(readCommand);

        // then
        assertTrue(isInvalid);
    }

    @Test
    @DisplayName("100 이상의 LBA 가 전달되는 경우 true 반환한다")
    void givenOutOfRangeLba100_whenCheckValidCommand_returnTrue(){
        // given
        setReadCommand("R", "100");

        // when
        boolean isInvalid = ssd.isInvalidCommand(readCommand);

        // then
        assertTrue(isInvalid);
    }

    @Test
    @DisplayName("lba 값이 유효하고 명령어가 R인 경우 false 반환한다")
    void givenValidLbaAndRead_whenCheckValidCommand_returnTrue(){
        // given
        setReadCommand("R", "99");

        // when
        boolean isInvalid = ssd.isInvalidCommand(readCommand);

        // then
        assertFalse(isInvalid);
    }

    @Test
    @DisplayName("lba 값이 유효하고 명령어가 W인 경우 데이터가 0x 로 시작하지 않으면 true 반환한다")
    void givenValidLbaAndWrite_whenCheckValidCommand_returnTrue(){
        // given
        setWriteCommand("W", "99", "0a01103302");

        // when
        boolean isInvalid = ssd.isInvalidCommand(writeCommand);

        // then
        assertTrue(isInvalid);
    }

    @Test
    @DisplayName("lba 값이 유효하고 명령어가 W인 경우 데이터 길이가 10이 아니면 true 반환한다")
    void givenValidLbaAndWriteAndLengthIsNot10_whenCheckValidCommand_returnTrue(){
        // given
        setWriteCommand("W", "99", "0x011033021");

        // when
        boolean isInvalid = ssd.isInvalidCommand(writeCommand);

        // then
        assertTrue(isInvalid);
    }

    @Test
    @DisplayName("lba 값이 유효하고 명령어가 W인 경우 데이터가 16진수 범위가 아니면 true 반환한다")
    void givenValidLbaAndWriteAndNotHexa_whenCheckValidCommand_returnTrue(){
        // given
        setWriteCommand("W", "99", "0x0119010K");

        // when
        boolean isInvalid = ssd.isInvalidCommand(writeCommand);

        // then
        assertTrue(isInvalid);
    }

    @Test
    @DisplayName("유효한 W 명령어가 전달된 경우 false 를 반환한다")
    void givenValidWrite_whenCheckValidCommand_returnTrue(){
        // given
        setWriteCommand("W", "99", "0x0119010A");

        // when
        boolean isInvalid = ssd.isInvalidCommand(writeCommand);

        // then
        assertFalse(isInvalid);
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
    void givenInvalid_lba_X_whenCheckValid_thenIsImpossibleToParseToInt() {
        // given


        // when
        setWriteCommand("W", "X", "0x12345678");

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
    @Disabled(value = "항상 파일이 존재해 무조건 적으로 발생하는 오류라 Disabled 작성")
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

    @Test
    void invalidArgumentTC01(){
        boolean isInvalid = ssd.isInvalidCommand(null);

        assertTrue(isInvalid);
    }

    @Test
    void invalidArgumentTC02(){
        String[] args = new String[1];
        args[0] = "W";

        boolean isInvalid = ssd.isInvalidCommand(args);

        assertTrue(isInvalid);
    }

    @Test
    void invalidArgumentTC03(){
        String[] args = new String[1];
        args[0] = "R";

        boolean isInvalid = ssd.isInvalidCommand(args);

        assertTrue(isInvalid);
    }

    @Test
    void invalidArgumentTC04(){
        String[] args = new String[2];
        args[0] = "W";
        args[1] = "33";

        boolean isInvalid = ssd.isInvalidCommand(args);

        assertTrue(isInvalid);
    }

    @Test
    void invalidArgumentTC05(){
        String[] args = new String[3];
        args[0] = "W";
        args[1] = "29";
        args[2] = "0x223123";

        boolean isInvalid = ssd.isInvalidCommand(args);

        assertTrue(isInvalid);
    }

    @Test
    void invalidArgumentTC06(){
        String[] args = new String[3];
        args[0] = "R";
        args[1] = "99";
        args[2] = "0x22312323";

        boolean isInvalid = ssd.isInvalidCommand(args);

        assertTrue(isInvalid);
    }

    @Test
    void invalidArgumentTC07(){
        String[] args = new String[2];
        args[0] = "R";
        args[1] = "W";

        boolean isInvalid = ssd.isInvalidCommand(args);

        assertTrue(isInvalid);
    }

    @Test
    void invalidArgumentTC08(){
        String[] args = new String[3];
        args[0] = "E";
        args[1] = "1";
        args[2] = "11";

        boolean isInvalid = ssd.isInvalidCommand(args);

        assertTrue(isInvalid);
    }

    @Test
    void invalidArgumentTC09(){
        String[] args = new String[3];
        args[0] = "E";
        args[1] = "91";
        args[2] = "10";

        int eraseLba = Integer.parseInt(args[1]);
        int eraseSize = Integer.parseInt(args[2]);

        if(eraseLba + eraseSize - 1 > 99){
            eraseSize = MAX_LBA + 1 - eraseLba;
        }

        assertThat(eraseSize).isEqualTo(9);
    }

    @Test
    void invalidArgumentTC10(){
        String[] args = new String[3];
        args[0] = "E";
        args[1] = "95";
        args[2] = "10";

        int eraseLba = Integer.parseInt(args[1]);
        int eraseSize = Integer.parseInt(args[2]);

        if(eraseLba + eraseSize - 1 > 99){
            eraseSize = MAX_LBA + 1 - eraseLba;
        }

        assertThat(eraseSize).isEqualTo(5);
    }

    private void setWriteCommand(String writeCode, String lba, String data){
        writeCommand[0] = writeCode;
        writeCommand[1] = lba;
        writeCommand[2] = data;
    }

    private void setReadCommand(String readCode, String lba){
        readCommand[0] = readCode;
        readCommand[1] = lba;
    }
}
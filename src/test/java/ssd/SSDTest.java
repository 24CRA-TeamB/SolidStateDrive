package ssd;

import org.junit.jupiter.api.BeforeEach;
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
    void doCommand() {
        setValidReadCommand();
        ssd.doCommand(readCommand);
        verify(ssdInterface, times(1)).read("1");
    }

    @Test
    void isImpossibleToParseToInt() {
        setInvalidWriteCommand();
        assertEquals(true, ssd.isImpossibleToParseToInt(writeCommand[1]));
    }

    private void setValidReadCommand() {
        readCommand[0] = "R";
        readCommand[1] = "1";
    }

    private void setInvalidWriteCommand() {
        writeCommand[0] = "W";
        writeCommand[1] = "X";
        writeCommand[2] = "0x12345678";
    }

    private void setValidWriteCommand() {
        writeCommand[0] = "W";
        writeCommand[1] = "1";
        writeCommand[2] = "0x12345678";
    }

    @Test
    void isInvalidLBA() {
        assertEquals(true, ssd.isInvalidLBA(MIN_LBA-1));
        assertEquals(true, ssd.isInvalidLBA(MAX_LBA+1));
    }

    @Test
    void doesNandFileExist() {
        assertEquals(false, new File(NAND_TXT_PATH).exists());
    }
}
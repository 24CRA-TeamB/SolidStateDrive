package ssd;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import java.io.*;

import static org.junit.jupiter.api.Assertions.fail;
import static org.assertj.core.api.Assertions.assertThat;

class SamsungSSDTest {
    public static final String READ_DATA_TARGET_FILE = "./nand.txt";
    public static final String SAMPLE_DATA_JSON_STRING = "[{\"data\":\"0x00000000\",\"lba\":\"0\"}, {\"data\":\"0x00000000\",\"lba\":\"1\"}]";

    @Test
    @Disabled("Command Factory에서 Invalid Argument Check 하도록 변경하여, 불가능한 Test Case로 판명")
    void readInvalidArgumentMinusLBATest(){
        Command command = new CommandRead("R", -1);
        command.execute();
    }

    @Test
    @Disabled("Command Factory에서 Invalid Argument Check 하도록 변경하여, 불가능한 Test Case로 판명")
    void readInvalidArgumentOverMaxLBATest(){
        Command command = new CommandRead("R", 100);
        command.execute();
    }

    @Test
    @Disabled("Command Factory에서 Invalid Argument Check 하도록 변경하여, 불가능한 Test Case로 판명")
    void readInterfaceLbaData(){
        Command command = new CommandRead("R", 14);
        command.execute();
    }

    @Test
    void readLbaData(){
        Command command = new CommandRead("R", 4);
        command.execute();
    }

    @Test
    void createCommandFactory(){
        assertThat(CommandFactory.getInstance()).isNotNull();
    }

    @Test
    @DisplayName("data 에 16진수 값이 넘는 값이 전달되면 RuntimeException 을 던진다")
    void writeThrowRuntimeExceptionWhenDataValueExceedInteger(){
        try {
            // when
            Command command = new CommandWrite("W", 1, "0x1298CDXF");
            command.execute();
            fail();
        } catch(RuntimeException e) {
            // then
            assertThat(e).isInstanceOf(RuntimeException.class);
        }
    }

    @Test
    @DisplayName("lba 에 0보다 작은 값이 전달되는 경우 RuntimeException 을 던진다")
    @Disabled("Command Factory에서 Invalid Argument Check 하도록 변경하여, 불가능한 Test Case로 판명")
    void writeThrowRuntimeExceptionWhenLbaValueExceedInteger(){
        try {
            // when
            Command command = new CommandWrite("W", -1, "0x12345678");
            command.execute();
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
            Command command = new CommandWrite("W", 1, "0012345678");
            command.execute();
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
            boolean deleteSuccess = file.delete();
            if(!deleteSuccess) fail("정상적으로 파일을 삭제하지 못 했습니다.");
        }

        // when
        Command command = new CommandWrite("W", 1, "0x12345678");
        command.execute();

        // then
        File nandTxt = new File(READ_DATA_TARGET_FILE);
        assertThat(nandTxt.exists()).isTrue();
    }

    @Test
    @DisplayName("lba 가 1, data 가 0x12345678 이 주어졌을 때, 1번 Json 객체의 데이터 값이 0x12345678")
    void givenSamsungSSD_whenWriteLba1_and_Data0x123456778_thenNandTxtContainResult(){
        try {
            // given
            createSampleNandTxt();

            // when
            Command command = new CommandWrite("W", 1, "0x12345678");
            command.execute();

            // then
            FileReader fileReader = new FileReader(READ_DATA_TARGET_FILE);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String jsonString = bufferedReader.readLine();

            JSONArray jsonArray = new JSONArray(jsonString);
            JSONObject jsonObject = (JSONObject) jsonArray.get(1);
            assertThat(jsonObject.get("data")).isEqualTo("0x12345678");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createSampleNandTxt(){
        try {
            FileWriter writer = new FileWriter(READ_DATA_TARGET_FILE);
            writer.write(SAMPLE_DATA_JSON_STRING);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
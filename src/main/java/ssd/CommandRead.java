package ssd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

public class CommandRead extends Command{

    private String cmd = "";
    private String lba = "";
    private String value = "";

    public CommandRead(String cmd, String lba) {
        this.cmd = cmd;
        this.lba = lba;
    }

    public String getCmd() {
        return cmd;
    }

    public String getLba() {
        return lba;
    }

    public String getValue() {
        return value;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public void setLba(String lba) {
        this.lba = lba;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    void execute() {
        String readValue = readNandFileForTargetLBA(getLba());
        writeResultFile(readValue);
    }

    private String readNandFileForTargetLBA(String lba) {
        if(isNandTxtNotExist()) return EMPTY_DATA_VALUE;

        String nandTextString = getNandTextString();

        if(isNandTextEmpty(nandTextString)) return EMPTY_DATA_VALUE;

        return findAndGetTargetLbaData(lba, nandTextString);
    }

    private void writeResultFile(String readValue) {
        try {
            FileWriter writer = new FileWriter(RESULT_TXT_PATH);
            writer.write(readValue);
            writer.close();
        } catch (IOException e) {
            System.out.println("파일 쓰기 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private boolean isNandTxtNotExist() {
        return !(new File(NAND_TXT_PATH).exists());
    }

    private String getNandTextString() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(NAND_TXT_PATH));

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(bufferedReader.readLine());

            bufferedReader.close();

            return stringBuilder.toString();
        } catch (IOException e) {
            System.out.println("파일을 읽는 도중 오류가 발생했습니다: " + e.getMessage());
        }

        return null;
    }

    private boolean isNandTextEmpty(String nandTextString) {
        return nandTextString == null;
    }

    private String findAndGetTargetLbaData(String lba, String nandTextString) {
        JSONArray jsonArray = new JSONArray(nandTextString);
        for(int jsonIdx=0; jsonIdx<jsonArray.length(); jsonIdx++){
            if(isTargetLBA(lba, jsonArray.getJSONObject(jsonIdx))){
                return getTargetLbaData(jsonArray.getJSONObject(jsonIdx));
            }
        }
        return EMPTY_DATA_VALUE;
    }

    private boolean isTargetLBA(String lba, JSONObject readData) {
        return readData.get("lba").equals(lba);
    }

    private String getTargetLbaData(JSONObject readData) {
        return readData.get("data").toString();
    }
}

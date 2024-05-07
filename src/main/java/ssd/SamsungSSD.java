package ssd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

public class SamsungSSD implements SSDInterface{

    public static final String EMPTY_DATA_VALUE = "0x00000000";
    public static final String READ_DATA_TARGET_FILE = "src/main/resources/nand.txt";
    public static final String WRITE_READ_DATA_TARGET_FILE = "src/main/resources/result.txt";

    @Override
    public void read(String lba) {
        String readValue = readNandFileForTargetLBA(lba);
        writeResultFile(readValue);
    }

    private void writeResultFile(String readValue) {
        try {
            FileWriter writer = new FileWriter(WRITE_READ_DATA_TARGET_FILE);
            writer.write(readValue);
            writer.close();
        } catch (IOException e) {
            System.out.println("파일 쓰기 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private String readNandFileForTargetLBA(String lba) {
        if(isNandTxtNotExist()) return EMPTY_DATA_VALUE;

        String nandTextString = getNandTextString();

        if(isNandTextEmpty(nandTextString)) return EMPTY_DATA_VALUE;

        return findAndGetTargetLbaData(lba, nandTextString);
    }

    private static boolean isNandTextEmpty(String nandTextString) {
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

    private String getNandTextString() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(READ_DATA_TARGET_FILE));

            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();

            return stringBuilder.toString();
        } catch (IOException e) {
            System.out.println("파일을 읽는 도중 오류가 발생했습니다: " + e.getMessage());
        }

        return null;
    }

    private boolean isNandTxtNotExist() {
        return !(new File(READ_DATA_TARGET_FILE).exists());
    }

    private String getTargetLbaData(JSONObject readData) {
        return readData.get("data").toString();
    }

    private boolean isTargetLBA(String lba, JSONObject readData) {
        return readData.get("lba").equals(lba);
    }

    @Override
    public void write(String lba, String data) {
        //File Write
    }
}

package ssd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

public class SamsungSSD implements SSDInterface{

    public static final String EMPTY_DATA_VALUE = "0x00000000";
    public static final String NAND_TXT_PATH = "src/main/resources/nand.txt";
    public static final String RESULT_TXT_PATH = "src/main/resources/result.txt";

    @Override
    public void read(String lba) {
        String readValue = readNandFileForTargetLBA(lba);
        writeResultFile(readValue);
    }

    @Override
    public void write(String lba, String data) {
        if(!isValidData(data))
            throw new RuntimeException();

        if(Integer.parseInt(lba) < 0)
            throw new RuntimeException();

        if(!doesNandFileExist()) {
            createNandFile();
        }

        try {
            FileReader fileReader = new FileReader(NAND_TXT_PATH);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            stringBuilder.append(line);

            bufferedReader.close();

            JSONArray jsonArray = new JSONArray(stringBuilder.toString());
            JSONObject jsonObject = jsonArray.getJSONObject(Integer.parseInt(lba));
            jsonObject.put("data", data);

            FileWriter fileWriter = new FileWriter(NAND_TXT_PATH);
            fileWriter.write(jsonArray.toString());
            fileWriter.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private boolean isNandTxtNotExist() {
        return !(new File(NAND_TXT_PATH).exists());
    }

    private String getTargetLbaData(JSONObject readData) {
        return readData.get("data").toString();
    }

    private boolean isTargetLBA(String lba, JSONObject readData) {
        return readData.get("lba").equals(lba);
    }

    private boolean doesNandFileExist() {
        File nandTxt = new File(NAND_TXT_PATH);
        return nandTxt.exists();
    }

    private void createNandFile() {
        File nandTxtFile = new File(NAND_TXT_PATH);
        try {
            if(nandTxtFile.createNewFile()){
                JSONArray jsonArray = new JSONArray();
                for(int idx = 0; idx < 100; ++idx){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("lba", Integer.toString(idx));
                    jsonObject.put("data", EMPTY_DATA_VALUE);
                    jsonArray.put(jsonObject);
                }
                FileWriter fileWriter = new FileWriter(NAND_TXT_PATH);
                fileWriter.write(jsonArray.toString());
                fileWriter.close();
            }
        } catch (IOException e) {
            System.out.println("파일을 생성하는 도중 오류가 발생했습니다.");
        }
    }

    private boolean isValidData(String data) {
        if(!(data.charAt(0)=='0' && data.charAt(1) == 'x'))
            return false;
        if(doesContainInvalidHexaDecimalChar(data))
            return false;
        return true;
    }

    private static boolean doesContainInvalidHexaDecimalChar(String data) {
        for(int i = 2; i < data.length(); i++){
            char c = data.charAt(i);
            if(!(('0' <= c && c <= '9') || ('A' <= c && c <= 'F')))
                return true;
        }
        return false;
    }
}

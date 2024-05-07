package ssd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

public class SamsungSSD implements SSDInterface{

    public static final String READ_DATA_TARGET_FILE = "src/main/resources/nand.txt";
    public static final String EMPTY_DATA_VALUE = "0x00000000";

    @Override
    public void read(String lba) {
        //File Read
        readNandFileForTargetLBA(lba);
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
            FileReader fileReader = new FileReader(READ_DATA_TARGET_FILE);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            stringBuilder.append(line);

            bufferedReader.close();

            JSONArray jsonArray = new JSONArray(stringBuilder.toString());
            JSONObject jsonObject = jsonArray.getJSONObject(Integer.parseInt(lba));
            jsonObject.put("data", data);

            FileWriter fileWriter = new FileWriter(READ_DATA_TARGET_FILE);
            fileWriter.write(jsonArray.toString());
            fileWriter.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readNandFileForTargetLBA(String lba) {
        try {
            FileReader fileReader = new FileReader(READ_DATA_TARGET_FILE);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                JSONObject readData = new JSONObject(line);
                if(isTargetLBA(lba, readData)){
                    return getTargetLBAData(readData);
                }
            }

            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("파일을 읽는 도중 오류가 발생했습니다: " + e.getMessage());
        }
        return EMPTY_DATA_VALUE;
    }

    private static String getTargetLBAData(JSONObject readData) {
        return readData.get("data").toString();
    }

    private static boolean isTargetLBA(String lba, JSONObject readData) {
        return readData.get("lba").equals(lba);
    }

    private boolean doesNandFileExist() {
        File nandTxt = new File(READ_DATA_TARGET_FILE);
        return nandTxt.exists();
    }

    private void createNandFile() {
        File nandTxtFile = new File(READ_DATA_TARGET_FILE);
        try {
            if(nandTxtFile.createNewFile()){
                JSONArray jsonArray = new JSONArray();
                for(int idx = 0; idx < 100; ++idx){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("lba", Integer.toString(idx));
                    jsonObject.put("data", EMPTY_DATA_VALUE);
                    jsonArray.put(jsonObject);
                }
                FileWriter fileWriter = new FileWriter(READ_DATA_TARGET_FILE);
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

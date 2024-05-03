package ssd;

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



    @Override
    public void write(String lba, String data) {
        //File Write
    }
}

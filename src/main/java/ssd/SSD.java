package ssd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class SSD {
    public static final int MIN_LBA = 0;
    public static final int MAX_LBA = 99;
    public static final String EMPTY_DATA_VALUE = "0x00000000";
    public static final String NAND_TXT_PATH = "..\\nand.txt";

    public static void main(String[] args) {

        if(!doesNandFileExist()) {
            createNandFile();
        }

        if(isInvalidCommand(args))
            return;

        doCommand(args);
    }

    private static void doCommand(String[] cmdArgs) {
        DeviceDriver deviceDriver = new DeviceDriver(new SamsungSSD());
        String readOrWriteCmd = cmdArgs[0];
        String targetLba = cmdArgs[1];

        switch (readOrWriteCmd){
            case "W":
                String targetValue = cmdArgs[2];
                deviceDriver.writeData(targetLba, targetValue);
                break;
            case "R":
                deviceDriver.readData(targetLba);
                break;
        }
    }

    private static boolean isCmdEmpty(String cmd) {
        return cmd.equals("") || cmd == null;
    }

    private static boolean isInvalidCommand(String[] cmdArgs){

        if(!(cmdArgs[0].equals("W") || cmdArgs[0].equals("R")))
            return true;

        if(isImpossibleToParseToInt(cmdArgs[1]))
            return true;

        if(isInvalidLBA(Integer.parseInt(cmdArgs[1])))
            return true;

        if(cmdArgs[0].equals("R"))
            return false;


        if(!cmdArgs[2].startsWith("0x"))
            return true;

        if(cmdArgs[2].length()!=10)
            return true;

        for(int i=2; i<cmdArgs[2].length(); i++){
            char c = cmdArgs[2].charAt(i);
            if(!(('0' <= c && c <= '9') || ('A' <= c && c <= 'F')))
                return true;
        }

        return false;
    }

    private static boolean isImpossibleToParseToInt(String lbaStr) {
        for(int i=0; i<lbaStr.length(); i++){
            char c = lbaStr.charAt(i);
            if(!('0' <= c && c <= '9'))
                return true;
        }
        return false;
    }

    private static boolean isInvalidLBA(int lbaAddress) {
        return lbaAddress < MIN_LBA || lbaAddress > MAX_LBA;
    }

    private static boolean doesNandFileExist() {
        File nandTxt = new File(NAND_TXT_PATH);
        return nandTxt.exists();
    }

    private static void createNandFile() {
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
                writeJsonArrayToNandTxtPath(jsonArray);
            }
        } catch (IOException e) {
            System.out.println("파일을 생성하는 도중 오류가 발생했습니다.");
        }
    }
    private static void writeJsonArrayToNandTxtPath(JSONArray jsonArray) throws IOException {
        FileWriter fileWriter = new FileWriter(NAND_TXT_PATH);
        fileWriter.write(jsonArray.toString());
        fileWriter.close();
    }
}

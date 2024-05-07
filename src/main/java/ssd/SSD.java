package ssd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SSD {
    public static final int MIN_LBA = 0;
    public static final int MAX_LBA = 99;
    public static final String EMPTY_DATA_VALUE = "0x00000000";
    public static final String NAND_TXT_PATH = "../nand.txt";

    static DeviceDriver deviceDriver;

    public SSD(SSDInterface ssdInterface) {
        deviceDriver = new DeviceDriver(ssdInterface);
    }

    public static void main(String[] args) {

        deviceDriver = new DeviceDriver(new SamsungSSD());

        if(isInvalidCommand(args))
            return;

        doCommand(args);
    }

    public static void doCommand(String[] cmdArgs) {
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

    public static boolean isInvalidCommand(String[] cmdArgs){

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

    public static boolean isImpossibleToParseToInt(String lbaStr) {
        boolean sign = (lbaStr.charAt(0) == '-' || lbaStr.charAt(0) == '+');
        for(int i= sign ? 1 : 0; i<lbaStr.length(); i++){
            char c = lbaStr.charAt(i);
            if(!('0' <= c && c <= '9'))
                return true;
        }
        return false;
    }

    public static boolean isInvalidLBA(int lbaAddress) {
        return lbaAddress < MIN_LBA || lbaAddress > MAX_LBA;
    }
}

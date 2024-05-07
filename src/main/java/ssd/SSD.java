package ssd;

import java.util.Scanner;

public class SSD {
    public static final int MIN_LBA = 0;
    public static final int MAX_LBA = 99;

    public static void main(String[] args) {
        DeviceDriver deviceDriver = new DeviceDriver(new SamsungSSD());
        Scanner scanner = new Scanner(System.in);

        while(true){
            String cmd = scanner.nextLine();
            if (cmd.equals("") || cmd == null){
                continue;
            }

            String[] cmdArgs = cmd.split(" ");

            if(isValidCommand(cmdArgs))
                continue;

            String readOrWriteCmd = cmdArgs[0];
            String targetLba = cmdArgs[1];
            String targetValue = cmdArgs[2];

            switch (readOrWriteCmd){
                case "W":
                    deviceDriver.writeData(targetLba, targetValue);
                    break;
                case "R":
                    deviceDriver.readData(targetLba);
                    break;
            }
        }
    }

    private static boolean isValidCommand(String[] cmdArgs){

        if(!(cmdArgs[0].equals("W") || cmdArgs[0].equals("R")))
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

    private static boolean isInvalidLBA(int lbaAddress) {
        return lbaAddress < MIN_LBA || lbaAddress > MAX_LBA;
    }
}

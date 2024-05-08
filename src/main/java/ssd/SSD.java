package ssd;

public class SSD {
    public static final int MIN_LBA = 0;
    public static final int MAX_LBA = 99;

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
            case "E":
                String eraseSize = cmdArgs[2];
                deviceDriver.eraseData(targetLba, eraseSize);
                break;
        }
    }

    public static boolean isInvalidCommand(String[] cmdArgs){
        if(cmdArgs == null || cmdArgs.length < 2) return true;

        if(cmdArgs[0].equals("R")){
            // length test
            if(cmdArgs.length != 2) return true;

            // parse int test
            if(isImpossibleToParseToInt(cmdArgs[1]))
                return true;

            // invalid lba test
            if(isInvalidLBA(Integer.parseInt(cmdArgs[1])))
                return true;

            return false;
        }
        else if(cmdArgs[0].equals("W")){
            // length test
            if(cmdArgs.length != 3) return true;

            // parse int test
            if(isImpossibleToParseToInt(cmdArgs[1]))
                return true;

            // invalid lba test
            if(isInvalidLBA(Integer.parseInt(cmdArgs[1])))
                return true;

            // data validation
            if(!cmdArgs[2].startsWith("0x") || cmdArgs[2].length()!=10)
                return true;

            // hexa validation
            for(int i=2; i<cmdArgs[2].length(); i++){
                char c = cmdArgs[2].charAt(i);
                if(!(('0' <= c && c <= '9') || ('A' <= c && c <= 'F')))
                    return true;
            }

            return false;
        }
        else if(cmdArgs[0].equals("E")){
            // length test
            if(cmdArgs.length != 3) return true;

            // parse int test
            if(isImpossibleToParseToInt(cmdArgs[1]))
                return true;

            // invalid lba test
            if(isInvalidLBA(Integer.parseInt(cmdArgs[1])))
                return true;

            return false;
        }
        else return true;
    }

    public static boolean isImpossibleToParseToInt(String lbaStr) {
        try {
            Integer.parseInt(lbaStr);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    public static boolean isInvalidLBA(int lbaAddress) {
        return lbaAddress < MIN_LBA || lbaAddress > MAX_LBA;
    }
}

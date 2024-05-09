package ssd;

public class CommandFactory {

    public static final int MIN_LBA = 0;
    public static final int MAX_LBA = 99;
    public static final int MIN_ERASE_SIZE = 1;
    public static final int MAX_ERASE_SIZE = 10;

    private static CommandFactory fac;
    public static CommandFactory getInstance() {
        if (fac == null) fac = new CommandFactory();
        return fac;
    }

    Command makeCommand(String[] args){
        if(isInvalidCommand(args))
            return new CommandVoid();

        switch (args[0]){
            case "R":
                return new CommandRead(args[0], args[1]);
            case "W":
                return new CommandWrite(args[0], args[1], args[2]);
            case "E":
                int eraseLba = Integer.parseInt(args[1]);
                int eraseSize = Integer.parseInt(args[2]);
                if(isExceedEraseRange(eraseLba, eraseSize)){
                    eraseSize = MAX_LBA + 1 - eraseLba;
                }
                return new CommandErase(args[0], args[1], String.valueOf(eraseSize));
            default:
                return new CommandVoid();
        }
    }

    public boolean isInvalidCommand(String[] cmdArgs){
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
            if(isImpossibleToParseToInt(cmdArgs[1]) || isImpossibleToParseToInt(cmdArgs[2]))
                return true;

            // invalid lba test
            if(isInvalidLBA(Integer.parseInt(cmdArgs[1])))
                return true;

            // invalid size
            if(isInvalidEraseSize(Integer.parseInt(cmdArgs[2])))
                return true;

            return false;
        }
        else return true;
    }

    private static boolean isExceedEraseRange(int lba, int size) {
        return lba + size - 1 > MAX_ERASE_SIZE;
    }

    private static boolean isInvalidEraseSize(int size) {
        return size < MIN_ERASE_SIZE || size > MAX_ERASE_SIZE;
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

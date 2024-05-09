package ssd;

public class ArgumentInvalidChecker {
    public static final int MIN_LBA = 0;
    public static final int MAX_LBA = 99;
    public static final int MIN_ERASE_SIZE = 1;
    public static final int MAX_ERASE_SIZE = 10;
    protected static final Logger logger = Logger.getInstance("./ssd");

    public static boolean checkArgument(String[] cmdArgs) {
        if(cmdArgs == null) return true;

        if(cmdArgs[0].equals("R")){
            if(cmdArgs.length != 2) {
                logger.writeLog("[ERROR] 유효하지 않은 R 명령어 인자. 인자의 개수가 2개가 아닙니다.");
                return true;
            }

            if(isImpossibleToParseToInt(cmdArgs[1])){
                logger.writeLog("[ERROR] 유효하지 않은 R 명령어 인자. 인자를 int 로 변환할 수 없습니다.");
                return true;
            }

            if(isInvalidLBA(Integer.parseInt(cmdArgs[1]))){
                logger.writeLog("[ERROR] 유효하지 않은 R 명령어 인자. lba 값에 16진수로 변환할 수 없는 값이 포함되어 있습니다.");
                return true;
            }

            return false;
        }
        else if(cmdArgs[0].equals("W")){
            if(cmdArgs.length != 3) {
                logger.writeLog("[ERROR] 유효하지 않은 W 명령어 인자. 인자 개수가 3이 아닙니다.");
                return true;
            }

            if(isImpossibleToParseToInt(cmdArgs[1])){
                logger.writeLog("[ERROR] 유효하지 않은 W 명령어 인자. 인자를 int 로 변환할 수 없습니다.");
                return true;
            }

            if(isInvalidLBA(Integer.parseInt(cmdArgs[1]))) {
                logger.writeLog("[ERROR] 유효하지 않은 W 명령어 인자. lba 값에 16진수로 변환할 수 없는 값이 포함되어 있습니다.");
                return true;
            }

            if(!cmdArgs[2].startsWith("0x") || cmdArgs[2].length()!=10) {
                logger.writeLog("[ERROR] 유효하지 않은 W 명령어 인자. lba 값이 0x 로 시작하지 않거나 길이가 다릅니다.");
                return true;
            }

            for(int i=2; i<cmdArgs[2].length(); i++){
                char c = cmdArgs[2].charAt(i);
                if(!(('0' <= c && c <= '9') || ('A' <= c && c <= 'F'))){
                    logger.writeLog("[ERROR] 유효하지 않은 W 명령어 인자. 16진수로 포함되지 않습니다.");
                    return true;
                }
            }

            return false;
        }
        else if(cmdArgs[0].equals("E")){
            if(cmdArgs.length != 3) {
                logger.writeLog("[ERROR] 유효하지 않은 E 명령어 인자. 인자 개수가 3이 아닙니다.");
                return true;
            }

            if(isImpossibleToParseToInt(cmdArgs[1]) || isImpossibleToParseToInt(cmdArgs[2])) {
                logger.writeLog("[ERROR] 유효하지 않은 E 명령어 인자. 인자를 int 로 변환할 수 없습니다.");
                return true;
            }

            if(isInvalidLBA(Integer.parseInt(cmdArgs[1]))) {
                logger.writeLog("[ERROR] 유효하지 않은 E 명령어 인자. lba 값에 16진수로 변환할 수 없는 값이 포함되어 있습니다.");
                return true;
            }

            if(isInvalidEraseSize(Integer.parseInt(cmdArgs[2]))) {
                logger.writeLog("[ERROR] 유효하지 않은 E 명령어 인자. range 범위가 적절하지 않습니다.");
                return true;
            }

            return false;
        }
        else if(cmdArgs[0].equals("F")){
            if(cmdArgs.length != 1) {
                logger.writeLog("[ERROR] 유효하지 않은 F 명령어 인자. F 값 외 다른 인자가 포함되어 있습니다.");
                return true;
            }

            return false;
        }
        else return true;
    }

    public static boolean isExceedEraseRange(int lba, int size) {
        return lba + size - 1 > MAX_LBA;
    }

    public static boolean isInvalidEraseSize(int size) {
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

package ssd;

public class SamsungSSD implements SSDInterface{

    @Override
    public void read(String lba) {
        //File Read
    }

    @Override
    public void write(String lba, String data) {
        if(!isValidData(data))
            throw new RuntimeException();

        if(Integer.parseInt(lba) < 0)
            throw new RuntimeException();
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

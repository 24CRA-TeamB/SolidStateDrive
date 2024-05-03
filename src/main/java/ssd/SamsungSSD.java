package ssd;

public class SamsungSSD implements SSDInterface{

    @Override
    public void read(String lba) {
        //File Read
    }

    @Override
    public void write(String lba, String data) {
        for(int i = 2; i < data.length(); i++){
            if(isHexaDecimal(data.charAt(i))) {
                throw new RuntimeException();
            }
        }

        if(Integer.parseInt(lba) < 0)
            throw new RuntimeException();
    }

    private static boolean isHexaDecimal(char c) {
        return !(('0' <= c && c <= '9') || ('A' <= c && c <= 'F'));
    }
}

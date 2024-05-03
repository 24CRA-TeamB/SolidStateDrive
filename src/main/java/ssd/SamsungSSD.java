package ssd;

public class SamsungSSD implements SSDInterface{

    @Override
    public void read(String lba) {
        //File Read
        int lbaAddress = Integer.parseInt(lba);
        checkInvalidLBAForRead(lbaAddress);
    }

    private void checkInvalidLBAForRead(int lbaAddress) {
        if(isInvalidLBA(lbaAddress)){
            throw new InvalidLBAExcpetion("LBA를 확인하세요 ");
        }
    }

    private boolean isInvalidLBA(int lbaAddress) {
        return lbaAddress < 0 || lbaAddress > 99;
    }

    @Override
    public void write(String lba, String data) {
        //File Write
    }
}

package ssd;

public class DeviceDriver {
    public static final int MIN_LBA = 0;
    public static final int MAX_LBA = 99;

    private SSDInterface ssdInterface;


    public DeviceDriver(SSDInterface ssdInterface) {
        this.ssdInterface = ssdInterface;
    }

    public void readData(String lba){
        if(isInvalidLBA(Integer.parseInt(lba))){
            return;
        }
        ssdInterface.read(lba);
    }

    private boolean isInvalidLBA(int lbaAddress) {
        return lbaAddress < MIN_LBA || lbaAddress > MAX_LBA;
    }

    public void writeData(String lba, String data){
        ssdInterface.write(lba, data);
    }

    public void eraseData(String targetLba, String eraseSize) {
        return;
    }
}

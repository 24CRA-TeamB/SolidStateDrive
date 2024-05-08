package ssd;

public class DeviceDriver {
    public static final int MIN_LBA = 0;
    public static final int MAX_LBA = 99;

    private SSDInterface ssdInterface;


    public DeviceDriver(SSDInterface ssdInterface) {
        this.ssdInterface = ssdInterface;
    }

    public void readData(String lba){
        ssdInterface.read(lba);
    }

    public void writeData(String lba, String data){
        ssdInterface.write(lba, data);
    }
}

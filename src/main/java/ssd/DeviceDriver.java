package java.ssd;

public class DeviceDriver {
    private SSDInterface ssdInterface;

    public DeviceDriver(SSDInterface ssdInterface) {
        this.ssdInterface = ssdInterface;
    }

    public void readData(int lba){
        ssdInterface.read(lba);
    }

    public void writeData(int lba, int data){
        ssdInterface.write(lba, data);
    }
}

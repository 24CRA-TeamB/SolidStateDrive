package ssd;

public class DeviceDriver {
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

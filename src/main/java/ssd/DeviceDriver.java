package ssd;

public class DeviceDriver {
    public static final int MIN_LBA = 0;
    public static final int MAX_LBA = 99;

    private SSDInterface ssdInterface;


    public DeviceDriver(SSDInterface ssdInterface) {
        this.ssdInterface = ssdInterface;
    }

    public void readData(Command command){
        ssdInterface.read(command);
    }

    public void writeData(Command command){
        ssdInterface.write(command);
    }

    public void eraseData(Command command){
        ssdInterface.erase(command);
    }
}

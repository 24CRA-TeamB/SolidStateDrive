package java.shell;

import java.ssd.DeviceDriver;
import java.ssd.SamsungSSD;

public class TestShellScript {
    public static void main(String[] args) {
        DeviceDriver deviceDriver = new DeviceDriver(new SamsungSSD());
        deviceDriver.writeData(10, 10);
        deviceDriver.readData(10);
    }
}

package java.shell;

import org.assertj.core.util.VisibleForTesting;

import java.ssd.DeviceDriver;
import java.ssd.SamsungSSD;

public class TestShellScript {
    public static void main(String[] args) {
        DeviceDriver deviceDriver = new DeviceDriver(new SamsungSSD());
        deviceDriver.writeData(10, 10);
        deviceDriver.readData(10);
    }

    @VisibleForTesting
    static void write() {

    }

    @VisibleForTesting
    static void fullwrite() {

    }

    @VisibleForTesting
    static void read() {

    }

    @VisibleForTesting
    static void fullwread() {

    }

    @VisibleForTesting
    static void exit() {

    }

    @VisibleForTesting
    static void help() {

    }
}

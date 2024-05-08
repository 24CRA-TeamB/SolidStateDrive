package ssd;

public class SSD {

    static DeviceDriver deviceDriver;
    static CommandFactory commandFactory;

    public SSD(SSDInterface ssdInterface) {
        deviceDriver = new DeviceDriver(ssdInterface);
    }

    public static void main(String[] args) {
        deviceDriver = new DeviceDriver(new SamsungSSD());
        commandFactory = CommandFactory.getInstance();

        Command command = commandFactory.makeCommand(args);
        if(command == null) return;

        doCommand(command);
    }

    public static void doCommand(Command command) {
        switch (command.getCmd()){
            case "W":
                deviceDriver.writeData(command);
                break;
            case "R":
                deviceDriver.readData(command);
                break;
            case "E":
                deviceDriver.eraseData(command);
                break;
        }
    }
}

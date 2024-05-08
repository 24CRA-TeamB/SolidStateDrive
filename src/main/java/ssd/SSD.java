package ssd;

public class SSD {

    static DeviceDriver deviceDriver;
    static CommandFactory commandFactory;
    static Buffer buffer;

    public SSD(SSDInterface ssdInterface) {
        deviceDriver = new DeviceDriver(ssdInterface);
    }

    public static void main(String[] args) {
        deviceDriver = new DeviceDriver(new SamsungSSD());
        commandFactory = CommandFactory.getInstance();
        buffer = new Buffer();

        Command command = commandFactory.makeCommand(args);
        if(command == null) return;

        buffer.addCommand(command);

        if(buffer.full() || args[0].equals("F")){
            flush(buffer);
        }
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

    private static void flush(Buffer buffer){
        while(!buffer.empty()){
            Command bufferedCommand = buffer.getCommand();
            doCommand(bufferedCommand);
        }
    }
}

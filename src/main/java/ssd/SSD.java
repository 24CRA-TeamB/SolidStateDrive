package ssd;

public class SSD {

    static DeviceDriver deviceDriver;
    static CommandFactory commandFactory;
    static CommandBuffer commandBuffer;

    public SSD(SSDInterface ssdInterface) {
        deviceDriver = new DeviceDriver(ssdInterface);
    }

    public static void main(String[] args) {
        deviceDriver = new DeviceDriver(new SamsungSSD());
        commandFactory = CommandFactory.getInstance();
        commandBuffer = new CommandBuffer();

        Command command = commandFactory.makeCommand(args);
        if(command == null) return;

        commandBuffer.addCommand(command);

        // flush 면 ADD 안 하고 flush 수행
        if(commandBuffer.full() || args[0].equals("F")){
            flush(commandBuffer);
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

    private static void flush(CommandBuffer commandBuffer){
        while(!commandBuffer.empty()){
            Command bufferedCommand = commandBuffer.getCommand();
            doCommand(bufferedCommand);
        }
    }
}

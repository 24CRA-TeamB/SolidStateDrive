package ssd;

public class SSD {

    static CommandFactory commandFactory;
    static CommandBuffer commandBuffer;

    public static void main(String[] args) {
        commandFactory = CommandFactory.getInstance();
        commandBuffer = new CommandBuffer();
        CommandFactory commandFactory = CommandFactory.getInstance();

        Command command = commandFactory.makeCommand(args);
        if(command == null) return;

        command.execute();
    }
}

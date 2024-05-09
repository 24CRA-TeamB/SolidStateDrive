package ssd;

public class SSD {
    public static void main(String[] args) {
        CommandFactory commandFactory = CommandFactory.getInstance();
        CommandBuffer buffer = new CommandBuffer(commandFactory);

        buffer.load();

        if(ArgumentInvalidChecker.checkArgument(args)){
            commandFactory.makeVoidCommand().execute();
            return;
        }

        CommandCode cmdCode = CommandCode.valueOf(args[0]);
        if(cmdCode.equals(CommandCode.F)){
            buffer.flush();
        }
        else if(cmdCode.equals(CommandCode.R)){
            Command command = commandFactory.makeCommand(args);
            buffer.read(command, args[1]);
        }
        else {
            Command command = commandFactory.makeCommand(args);
            buffer.addCommand(command);
        }

        if(buffer.full()){
            buffer.flush();
        }

        buffer.store();
    }
}
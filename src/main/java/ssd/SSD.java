package ssd;

public class SSD {
    public static void main(String[] args) {
        CommandFactory commandFactory = CommandFactory.getInstance();
        Command command = commandFactory.makeCommand(args);
        command.execute();
    }
}

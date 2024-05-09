package ssd;

import org.json.JSONObject;

public class CommandFactory {
    private static CommandFactory fac;
    public static CommandFactory getInstance() {
        if (fac == null) fac = new CommandFactory();
        return fac;
    }

    Command makeCommand(String[] args){
        Command command;
        CommandCode code = CommandCode.valueOf(args[0]);

        switch (code){
            case R:
                String readCmd = args[0];
                int readLba = Integer.parseInt(args[1]);
                command = new CommandRead(readCmd, readLba);
                break;

            case W:
                int writeLba = Integer.parseInt(args[1]);
                String writeData = args[2];
                command = new CommandWrite(args[0], writeLba, writeData);
                break;

            case E:
                int eraseLba = Integer.parseInt(args[1]);
                int eraseSize = Integer.parseInt(args[2]);
                if(ArgumentInvalidChecker.isExceedEraseRange(eraseLba, eraseSize))
                    eraseSize = ArgumentInvalidChecker.MAX_LBA + 1 - eraseLba;
                command = new CommandErase(args[0], eraseLba, eraseSize);
                break;

            default:
                command = new CommandVoid();
        }
        return command;
    }

    Command makeCommand(JSONObject jsonObject){
        Command command;
        CommandCode code = CommandCode.valueOf(jsonObject.getString("cmd"));

        switch (code){
            case R:
                int readLba = Integer.parseInt(jsonObject.getString("lba"));
                command = new CommandRead(CommandCode.R.getCmd(), readLba);
                break;

            case W:
                int writeLba = Integer.parseInt(jsonObject.getString("lba"));
                String writeData = jsonObject.getString("data");
                command = new CommandWrite(CommandCode.W.getCmd(), writeLba, writeData);
                break;

            case E:
                int eraseLba = Integer.parseInt(jsonObject.getString("lba"));
                int eraseSize = Integer.parseInt(jsonObject.getString("size"));
                if(ArgumentInvalidChecker.isExceedEraseRange(eraseLba, eraseSize))
                    eraseSize = ArgumentInvalidChecker.MAX_LBA + 1 - eraseLba;
                command = new CommandErase(CommandCode.E.getCmd(), eraseLba, eraseSize);
                break;
            default:
                command = new CommandVoid();
        }
        return command;
    }

    Command makeVoidCommand(){
        return new CommandVoid();
    }
}

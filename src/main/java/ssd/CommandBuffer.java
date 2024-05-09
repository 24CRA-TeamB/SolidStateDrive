package ssd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandBuffer {
    public static final int MAX_BUFFER_SIZE = 10;
    private String BUFFER_PATH = "./buffer.txt";
    CommandFactory commandFactory;
    List<Command> commands;

    public CommandBuffer(){
        this.commands = new ArrayList<>();
        commandFactory = CommandFactory.getInstance();

        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            bufferedReader = new BufferedReader(new FileReader(BUFFER_PATH));
            stringBuilder.append(bufferedReader.readLine());
            bufferedReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage() + " buffer.txt 없습니다.");
        }

        JSONArray jasonArray = new JSONArray(stringBuilder.toString());
        for(int i = 0; i < jasonArray.length(); i++){
            Command command = getCommandFromJsonObject(jasonArray.getJSONObject(i));
            this.commands.add(command);
        }
    }

    public void addCommand(Command command){
        this.commands.add(command);
        optimize();
    }

    public boolean empty(){
        return this.commands.isEmpty();
    }

    public boolean full(){
        return this.commands.size() == MAX_BUFFER_SIZE;
    }

    public Command getCommand(){
        // empty 일 때는 null 리턴하도록 변경

        Command command = this.commands.get(0);
        commands.remove(0);
        return command;
    }

    private void optimize(){

    }

    private Command getCommandFromJsonObject(JSONObject jsonObject){
        String cmd = jsonObject.getString("cmd");
        String lba = jsonObject.getString("lba");
        String value = jsonObject.getString("value");

        String[] args = new String[3];
        args[0] = cmd;
        args[1] = lba;
        args[2] = value;
        return commandFactory.makeCommand(args);
    }
}

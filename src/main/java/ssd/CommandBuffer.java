package ssd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandBuffer {
    private String BUFFER_PATH = "./buffer.txt";
    List<Command> commands;

    public CommandBuffer(){
        this.commands = new ArrayList<>();

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
        return this.commands.size() == 10;
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
        return new Command(cmd, lba, value);
    }
}

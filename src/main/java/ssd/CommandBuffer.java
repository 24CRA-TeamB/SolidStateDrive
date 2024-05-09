package ssd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CommandBuffer {
    public static final int MAX_BUFFER_SIZE = 10;
    private static final String BUFFER_TXT_PATH = "./buffer.txt";
    protected static final Logger logger = Logger.getInstance("./ssd");

    private final List<Command> commands;
    private final CommandFactory commandFactory;

    public CommandBuffer(CommandFactory commandFactory){
        this.commands = new ArrayList<>();
        this.commandFactory = commandFactory;
    }

    public void execute(){
        for(Command command : commands)
            command.execute();
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

    private void optimize(){}

    public void clearHistory() {
        this.commands.clear();
    }

    public int getSize(){
        return this.commands.size();
    }

    public void load(){
        File file = new File(BUFFER_TXT_PATH);
        if(!file.exists()){
            try {
                boolean success = file.createNewFile();
                if(!success){
                    return;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            bufferedReader = new BufferedReader(new FileReader(BUFFER_TXT_PATH));
            stringBuilder.append(bufferedReader.readLine());
            bufferedReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage() + " buffer.txt 없습니다.");
        }

        String jsonArrayString = stringBuilder.toString();

        if(jsonArrayString.isEmpty() || jsonArrayString.equals("null")){
            jsonArrayString = "[]";
        }

        JSONArray jasonArray = new JSONArray(jsonArrayString);
        for(int i = 0; i < jasonArray.length(); i++){
            JSONObject jsonObject = jasonArray.getJSONObject(i);
            Command command = commandFactory.makeCommand(jsonObject);
            this.commands.add(command);
        }
    }

    public void store() {
        JSONArray jsonArray = new JSONArray();
        for(Command command : this.commands){
            if(command instanceof CommandRead){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("cmd", CommandCode.R.getCmd());
                jsonObject.put("lba", Integer.toString(((CommandRead) command).getLba()));
                jsonArray.put(jsonObject);
            }

            if(command instanceof CommandWrite){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("cmd", CommandCode.W.getCmd());
                jsonObject.put("lba", Integer.toString(((CommandWrite) command).getLba()));
                jsonObject.put("data", ((CommandWrite) command).getData());
                jsonArray.put(jsonObject);
            }

            if(command instanceof CommandErase){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("cmd", CommandCode.E.getCmd());
                jsonObject.put("lba", Integer.toString(((CommandErase) command).getLba()));
                jsonObject.put("size", Integer.toString(((CommandErase) command).getSize()));
                jsonArray.put(jsonObject);
            }
        }

        try {
            FileWriter fileWriter = new FileWriter(BUFFER_TXT_PATH);
            fileWriter.write(jsonArray.toString());
            fileWriter.close();
        } catch (IOException e){
            logger.writeLog("[ERROR] buffer.txt Write 도중 문제가 발생했습니다.");
        }
    }

    public void flush() {
        logger.writeLog("[SUCCESS] Flush");
    }
}

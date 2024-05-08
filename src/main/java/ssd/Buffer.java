package ssd;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Buffer {
    private String BUFFER_PATH = "./buffer.txt";
    List<Command> commands;

    public Buffer(){
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            bufferedReader = new BufferedReader(new FileReader(BUFFER_PATH));
            stringBuilder.append(bufferedReader.readLine());
            bufferedReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage() + " buffer.txt 없습니다.");
        }

        this.commands = new ArrayList<>();
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
        Command command = this.commands.get(0);
        commands.remove(0);
        return command;
    }

    private void optimize(){

    }
}

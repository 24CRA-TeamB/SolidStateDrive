package ssd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

public class CommandVoid extends Command{

    @Override
    void execute() {
        initResultFile();
    }

    private void initResultFile() {
        writeResultFile("");
    }

    private void writeResultFile(String readValue) {
        try {
            FileWriter writer = new FileWriter(RESULT_TXT_PATH);
            writer.write(readValue);
            writer.close();
        } catch (IOException e) {
            System.out.println("파일 쓰기 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}

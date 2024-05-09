package ssd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

public class CommandErase extends Command{

    private String cmd = "";
    private String lba = "";
    private String value = "";

    public CommandErase(String cmd, String lba, String value) {
        this.cmd = cmd;
        this.lba = lba;
        this.value = value;
    }

    public String getCmd() {
        return cmd;
    }

    public String getLba() {
        return lba;
    }

    public String getValue() {
        return value;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public void setLba(String lba) {
        this.lba = lba;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    void execute() {
        for(int startLba = Integer.parseInt(getLba()); startLba<(Integer.parseInt(getValue())+Integer.parseInt(getLba())); startLba++){
            writeNandTxtFile(String.valueOf(startLba), EMPTY_DATA_VALUE);
        }
    }

    private void writeNandTxtFile(String lba, String data) {
        try {
            FileReader fileReader = new FileReader(NAND_TXT_PATH);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            stringBuilder.append(line);

            bufferedReader.close();

            JSONArray jsonArray = new JSONArray(stringBuilder.toString());
            JSONObject jsonObject = jsonArray.getJSONObject(Integer.parseInt(lba));
            jsonObject.put("data", data);

            writeJsonArrayToNandTxtPath(jsonArray);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeJsonArrayToNandTxtPath(JSONArray jsonArray) throws IOException {
        FileWriter fileWriter = new FileWriter(NAND_TXT_PATH);
        fileWriter.write(jsonArray.toString());
        fileWriter.close();
    }
}

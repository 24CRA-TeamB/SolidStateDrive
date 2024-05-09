package ssd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

public class CommandWrite extends Command{

    private String cmd = "";
    private String lba = "";
    private String value = "";

    public CommandWrite(String cmd, String lba, String value) {
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
        if(!isValidData(getValue()))
            throw new RuntimeException();

        if(Integer.parseInt(getLba()) < 0)
            throw new RuntimeException();

        writeNandTxtFile(getLba(), getValue());
    }

    private boolean isValidData(String data) {
        if(!(data.charAt(0)=='0' && data.charAt(1) == 'x'))
            return false;
        if(hasInvalidHexaDecimalChar(data))
            return false;
        return true;
    }

    private boolean hasInvalidHexaDecimalChar(String data) {
        for(int i = 2; i < data.length(); i++){
            char c = data.charAt(i);
            if(!(('0' <= c && c <= '9') || ('A' <= c && c <= 'F')))
                return true;
        }
        return false;
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

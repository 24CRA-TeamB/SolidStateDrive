package ssd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

public class CommandWrite extends Command{

    private String cmd;
    private int lba;
    private String data;

    public CommandWrite(String cmd, int lba, String data) {
        this.cmd = cmd;
        this.lba = lba;
        this.data = data;
    }

    public String getCmd() {
        return cmd;
    }

    public int getLba() {
        return lba;
    }

    public String getData() {
        return data;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public void setLba(int lba) {
        this.lba = lba;
    }

    public void setData(String value) {
        this.data = data;
    }

    @Override
    void execute() {
        writeNandTxtFile(Integer.toString(this.lba), this.data);
        logger.writeLog("[SUCCESS] LBA: "+getLba() +", Data: "+ getData());
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
            logger.writeLog("[ERROR] nand.txt 파일을 찾지 못했습니다.");
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.writeLog("[ERROR] nand.txt Write 시 문제가 발생했습니다.");
            throw new RuntimeException(e);
        }
    }

    private void writeJsonArrayToNandTxtPath(JSONArray jsonArray) throws IOException {
        FileWriter fileWriter = new FileWriter(NAND_TXT_PATH);
        fileWriter.write(jsonArray.toString());
        fileWriter.close();
    }
}

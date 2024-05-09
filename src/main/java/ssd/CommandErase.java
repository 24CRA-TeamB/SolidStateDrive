package ssd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

public class CommandErase extends Command{

    private String cmd;
    private int lba;
    private int size;

    public CommandErase(String cmd, int lba, int size) {
        this.cmd = cmd;
        this.lba = lba;
        this.size = size;
    }

    public String getCmd() {
        return cmd;
    }

    public int getLba() {
        return lba;
    }

    public int getSize() {
        return size;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public void setLba(int lba) {
        this.lba = lba;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    void execute() {
        for(int startLba = this.lba; startLba < this.lba + this.size; startLba++){
            writeNandTxtFile(String.valueOf(startLba), EMPTY_DATA_VALUE);
        }
        logger.writeLog("[SUCCESS] StartLBA: "+getLba()+", Size: " + getSize());
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

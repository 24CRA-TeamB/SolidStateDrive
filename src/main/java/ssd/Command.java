package ssd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

abstract class Command {

    public static final String EMPTY_DATA_VALUE = "0x00000000";
    public static final String NAND_TXT_PATH = "./nand.txt";
    public static final String RESULT_TXT_PATH = "./result.txt";
    protected static final Logger logger = Logger.getInstance("./ssd");

    abstract void execute();

    public Command() {
        createNandTextFile();
    }

    private void createNandTextFile() {
        if(!doesNandFileExist()) {
            createNandFile();
        }
    }

    private boolean doesNandFileExist() {
        File nandTxt = new File(NAND_TXT_PATH);
        return nandTxt.exists();
    }

    private void createNandFile() {
        File nandTxtFile = new File(NAND_TXT_PATH);
        try {
            if(nandTxtFile.createNewFile()){
                JSONArray jsonArray = new JSONArray();
                for(int idx = 0; idx < 100; ++idx){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("lba", Integer.toString(idx));
                    jsonObject.put("data", EMPTY_DATA_VALUE);
                    jsonArray.put(jsonObject);
                }
                writeJsonArrayToNandTxtPath(jsonArray);
            }
        } catch (IOException e) {
            logger.writeLog("[ERROR] nand.txt 파일을 생성하는 도중 오류가 발생했습니다.");
        }
    }

    private void writeJsonArrayToNandTxtPath(JSONArray jsonArray) throws IOException {
        FileWriter fileWriter = new FileWriter(NAND_TXT_PATH);
        fileWriter.write(jsonArray.toString());
        fileWriter.close();
    }


}

package ssd;

public class Command {
    private String cmd = "";
    private String lba = "";
    private String value = "";

    public Command() {
    }

    public Command(String cmd, String lba) {
        this.cmd = cmd;
        this.lba = lba;
    }

    public Command(String cmd, String lba, String value) {
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
}

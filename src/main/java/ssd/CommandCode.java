package ssd;

public enum CommandCode {
    R("R"),
    W("W"),
    E("E"),
    F("F");

    final String cmd;

    CommandCode(String cmd) {
        this.cmd = cmd;
    }

    public String getCmd() {
        return cmd;
    }
}

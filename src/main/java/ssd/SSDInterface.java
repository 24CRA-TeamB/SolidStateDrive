package ssd;

public interface SSDInterface {
    void read(String lba);
    void write(String lba, String data);
}

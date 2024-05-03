package ssd;

public interface SSDInterface {
    void read(int lba);
    void write(int lba, int data);
}

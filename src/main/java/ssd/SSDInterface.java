package ssd;

public interface SSDInterface {
    void read(String lba) throws InvalidLBAExcpetion;
    void write(String lba, String data);
}

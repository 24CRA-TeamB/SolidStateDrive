package ssd;

public interface SSDInterface {
    void read(Command command);
    void write(Command command);
    void erase(Command command);
}

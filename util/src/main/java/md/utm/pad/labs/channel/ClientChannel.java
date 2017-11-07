package md.utm.pad.labs.channel;

public interface ClientChannel {
    String readLine();
    void write(String data);
    void close();

    void writeNoBreak(String data);
}

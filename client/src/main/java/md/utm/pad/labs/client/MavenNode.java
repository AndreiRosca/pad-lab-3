package md.utm.pad.labs.client;

/**
 * Created by anrosca on Nov, 2017
 */
public class MavenNode {
    private final String address;
    private final int port;

    public MavenNode(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "MavenNode{" +
                "address='" + address + '\'' +
                ", port=" + port +
                '}';
    }
}

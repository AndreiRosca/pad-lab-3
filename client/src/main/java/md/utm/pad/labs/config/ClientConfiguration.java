package md.utm.pad.labs.config;

public class ClientConfiguration {
    private String nodeMulticastAddress = "230.1.1.1";
    private int nodePort = 9999;
    private int nodeResponseTimeoutInMilliseconds = 1000 * 10;
    private int clientPort = 9090;

    public String getNodeMulticastAddress() {
        return nodeMulticastAddress;
    }

    public int getNodePort() {
        return nodePort;
    }

    public long getNodeResponseTimeout() {
        return nodeResponseTimeoutInMilliseconds;
    }

    public int getClientPort() {
        return clientPort;
    }
}

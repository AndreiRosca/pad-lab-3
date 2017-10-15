package md.utm.pad.labs.node.config;

public class NodeConfiguration {
    private int clientPort = 9090;
    private int nodePort = 9999;
    private String nodeGroupAddress = "230.1.1.1";

    public int getClientPort() {
        return clientPort;
    }

    public int getNodePort() {
        return nodePort;
    }

    public String getNodeGroupAddress() {
        return nodeGroupAddress;
    }
}

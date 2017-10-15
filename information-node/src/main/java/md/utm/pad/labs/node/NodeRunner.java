package md.utm.pad.labs.node;

import md.utm.pad.labs.node.config.NodeConfiguration;

import java.io.IOException;

public class NodeRunner {
    public static void main(String[] args) throws IOException {
        NodeServer server = new NodeServer(new NodeConfiguration("/configuration.properties"));
        server.start();
        System.out.println("Press any key to stop the node...");
        System.in.read();
        server.stop();
    }
}

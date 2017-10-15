package md.utm.pad.labs.node;

import md.utm.pad.labs.handler.DefaultClientHandler;
import md.utm.pad.labs.node.config.NodeConfiguration;
import md.utm.pad.labs.service.impl.JacksonJsonService;

import java.io.IOException;

public class NodeRunner {
    public static void main(String[] args) throws IOException {
        NodeServer server = new NodeServer(new NodeConfiguration("/configuration.properties"),
                new JacksonJsonService(), new DefaultClientHandler());
        server.start();
        System.out.println("Press any key to stop the node...");
        System.in.read();
        server.stop();
    }
}

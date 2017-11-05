package md.utm.pad.labs.node;

import md.utm.pad.labs.handler.DefaultClientHandler;
import md.utm.pad.labs.node.config.NodeConfiguration;
import md.utm.pad.labs.node.context.InMemoryNodeContext;
import md.utm.pad.labs.repository.InMemoryStudentRepository;
import md.utm.pad.labs.service.impl.JacksonJsonService;

import java.io.IOException;

public class NodeRunner {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Too few arguments. Usage: NodeRunner /configuration.properties");
            System.exit(-1);
        }
        NodeConfiguration config = new NodeConfiguration(args[0]);
        NodeServer server = new NodeServer(config, new JacksonJsonService(),
                new DefaultClientHandler(new InMemoryNodeContext(new InMemoryStudentRepository(), config)));
        server.start();
        System.out.println("Press any key to stop the node...");
        System.in.read();
        server.stop();
    }
}

package md.utm.pad.labs;

import md.utm.pad.labs.config.ClientConfiguration;
import md.utm.pad.labs.interogator.NodeInterogator;
import md.utm.pad.labs.service.impl.JacksonJsonService;

public class ClientDemo {
    public static void main(String[] args) {
        NodeInterogator interogator = new NodeInterogator(new ClientConfiguration("/configuration.properties"),
                new JacksonJsonService());
        interogator.interogateNodes();
        System.out.println(interogator.getNodes());
    }
}

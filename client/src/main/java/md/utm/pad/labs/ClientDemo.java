package md.utm.pad.labs;

import md.utm.pad.labs.config.ClientConfiguration;
import md.utm.pad.labs.interogator.NodeInterogator;
import md.utm.pad.labs.response.Response;
import md.utm.pad.labs.service.impl.JacksonJsonService;

import java.util.Comparator;
import java.util.Optional;

public class ClientDemo {
    public static void main(String[] args) {
        NodeInterogator interogator = new NodeInterogator(new ClientConfiguration("/configuration.properties"),
                new JacksonJsonService());
        interogator.interogateNodes();
        System.out.println(interogator.getNodes());
        Optional<Response> mavenNode = interogator.getNodes().stream()
                .sorted()
                .findFirst();
        System.out.println(mavenNode);

    }
}

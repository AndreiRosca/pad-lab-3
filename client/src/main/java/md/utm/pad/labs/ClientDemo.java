package md.utm.pad.labs;

import md.utm.pad.labs.client.Client;
import md.utm.pad.labs.config.ClientConfiguration;
import md.utm.pad.labs.service.impl.JacksonJsonService;

public class ClientDemo {
    public static void main(String[] args) throws Exception {
        Client client = new Client(new ClientConfiguration("/configuration.properties"),
                new JacksonJsonService());
        client.detectMavenNode();
        client.connectToMaven();
        client.getAll();
        client.close();
    }
}

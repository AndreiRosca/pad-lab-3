package md.utm.pad.labs;

import md.utm.pad.labs.config.MediatorConfiguration;
import md.utm.pad.labs.config.NodeClientConfiguration;
import md.utm.pad.labs.mediator.Mediator;
import md.utm.pad.labs.service.impl.JacksonJsonService;
import md.utm.pad.labs.service.impl.JaxbXmlService;

public class MediatorDemo {
    public static void main(String[] args) throws Exception {
        Mediator mediator = Mediator.newBuilder()
                .setConfiguration(new NodeClientConfiguration("/configuration.properties"))
                .setJsonService(new JacksonJsonService())
                .setMediatorConfiguration(new MediatorConfiguration("/mediator.properties"))
                .setXmlService(new JaxbXmlService())
                .build();
        mediator.init();
        System.out.println("Press any key to stop the mediator");
        System.in.read();
        mediator.close();
    }
}

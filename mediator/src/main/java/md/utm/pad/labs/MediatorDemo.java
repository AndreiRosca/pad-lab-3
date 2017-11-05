package md.utm.pad.labs;

import md.utm.pad.labs.config.MediatorConfiguration;
import md.utm.pad.labs.config.NodeClientConfiguration;
import md.utm.pad.labs.mediator.Mediator;
import md.utm.pad.labs.service.impl.JacksonJsonService;

public class MediatorDemo {
    public static void main(String[] args) throws Exception {
        Mediator mediator = new Mediator(new NodeClientConfiguration("/configuration.properties"),
                new JacksonJsonService(), new MediatorConfiguration("/mediator.properties"));
        mediator.init();
        System.out.println("Press any key to stop the mediator");
        System.in.read();
        mediator.close();
    }
}

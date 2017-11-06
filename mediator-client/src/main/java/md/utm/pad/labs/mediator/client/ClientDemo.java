package md.utm.pad.labs.mediator.client;

import md.utm.pad.labs.config.MediatorConfiguration;
import md.utm.pad.labs.domain.Student;
import md.utm.pad.labs.service.impl.JacksonJsonService;
import md.utm.pad.labs.service.impl.JaxbXmlService;

import java.util.List;

/**
 * Created by anrosca on Nov, 2017
 */
public class ClientDemo {
    public static void main(String[] args) {
        Client client = Client.newBuilder()
                .setConfiguration(new MediatorConfiguration("/mediator.properties"))
                .setJsonService(new JacksonJsonService())
                .setXmlService(new JaxbXmlService())
                .build();
        client.setUpMediatorConnection();
        List<Student> students = client.send("from Student order by name");
        System.out.println(students);
        client.close();
    }
}

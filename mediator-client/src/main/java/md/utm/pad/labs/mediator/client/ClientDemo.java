package md.utm.pad.labs.mediator.client;

import md.utm.pad.labs.config.MediatorConfiguration;
import md.utm.pad.labs.domain.Student;
import md.utm.pad.labs.service.impl.JacksonJsonService;

import java.util.List;

/**
 * Created by anrosca on Nov, 2017
 */
public class ClientDemo {
    public static void main(String[] args) {
        Client client = new Client(new MediatorConfiguration("/mediator.properties"), new JacksonJsonService());
        List<Student> students = client.send("from Student order by name");
        System.out.println(students);
        client.close();
    }
}

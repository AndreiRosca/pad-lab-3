package md.utm.pad.labs.mediator.client;

import md.utm.pad.labs.channel.ClientChannel;
import md.utm.pad.labs.channel.SocketClientChannel;
import md.utm.pad.labs.channel.util.ChannelUtil;
import md.utm.pad.labs.config.MediatorConfiguration;
import md.utm.pad.labs.domain.Student;
import md.utm.pad.labs.response.Response;
import md.utm.pad.labs.service.JsonService;
import md.utm.pad.labs.service.XmlService;
import md.utm.pad.labs.validator.XmlValidator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by anrosca on Nov, 2017
 */
public class Client implements AutoCloseable {
    private MediatorConfiguration configuration;
    private JsonService jsonService;
    private XmlService xmlService;
    private ClientChannel clientChannel;

    private Client() {
    }

    public void setUpMediatorConnection() {
        try {
            Socket socket = new Socket(InetAddress.getByName(configuration.getMediatorAddress()), configuration.getMediatorPort());
            clientChannel = new SocketClientChannel(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        clientChannel.close();
    }

    public List<Student> send(String dsl) {
        clientChannel.write(dsl);
        Optional<String> jsonResponse = ChannelUtil.readRequest(clientChannel);
        if (jsonResponse.isPresent()) {
            String xmlResponse = jsonResponse.get();
            XmlValidator xmlValidator = new XmlValidator(Response.class, "response.xsd");
            if (xmlValidator.validate(xmlResponse)) {
                Response response = xmlService.fromXml(xmlResponse, Response.class);
                return response.getResponseData();
            }
        }
        return Collections.emptyList();
    }

    public static ClientBuilder newBuilder() {
        return new ClientBuilder();
    }

    public static final class ClientBuilder {
        private MediatorConfiguration configuration;
        private JsonService jsonService;
        private XmlService xmlService;

        private ClientBuilder() {
        }

        public ClientBuilder setConfiguration(MediatorConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        public ClientBuilder setJsonService(JsonService jsonService) {
            this.jsonService = jsonService;
            return this;
        }

        public ClientBuilder setXmlService(XmlService xmlService) {
            this.xmlService = xmlService;
            return this;
        }

        public Client build() {
            Client client = new Client();
            client.xmlService = this.xmlService;
            client.jsonService = this.jsonService;
            client.configuration = this.configuration;
            return client;
        }
    }
}

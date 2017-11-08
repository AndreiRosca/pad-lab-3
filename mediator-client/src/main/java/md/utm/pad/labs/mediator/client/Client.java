package md.utm.pad.labs.mediator.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import javafx.util.Pair;
import md.utm.pad.labs.channel.ClientChannel;
import md.utm.pad.labs.channel.ResponseUtil;
import md.utm.pad.labs.channel.SocketClientChannel;
import md.utm.pad.labs.config.MediatorConfiguration;
import md.utm.pad.labs.domain.Student;
import md.utm.pad.labs.response.Response;
import md.utm.pad.labs.service.JsonService;
import md.utm.pad.labs.service.XmlService;
import md.utm.pad.labs.validator.JsonValidator;
import md.utm.pad.labs.validator.XmlValidator;
import org.apache.log4j.Logger;

import javax.xml.validation.Schema;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.List;


/**
 * Created by anrosca on Nov, 2017
 */
public class Client implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(Client.class);

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
        //sendDslAcceptXml(dsl);
        sendDslAcceptJson(dsl);
        Pair<String, String> responseData = ResponseUtil.readResponse(clientChannel);
        if (ResponseUtil.isResponseJson(responseData.getKey())) {
            String jsonResponse = responseData.getValue();
            if (JsonValidator.validate(jsonResponse, "/schema.json")) {
                Response response = jsonService.fromJson(jsonResponse, Response.class);
                return response.getResponseData();
            } else {
                LOGGER.error("Got an invalid json response.");
            }
        }
        else if (ResponseUtil.isResponseXml(responseData.getKey())) {
            String xmlResponse = responseData.getValue();
            XmlValidator xmlValidator = new XmlValidator(Response.class, "response.xsd");
            if (xmlValidator.validate(xmlResponse)) {
                Response response = xmlService.fromXml(xmlResponse, Response.class);
                return response.getResponseData();
            } else {
                LOGGER.error("Got an invalid xml response.");
            }
        }
        return Collections.emptyList();
    }

    private void sendDslAcceptJson(String dsl) {
        clientChannel.writeNoBreak(String.format("Accept: %s", jsonService.getMediaType()));
        clientChannel.write(dsl);
    }

    private void sendDslAcceptXml(String dsl) {
        clientChannel.writeNoBreak(String.format("Accept: %s", xmlService.getMediaType()));
        clientChannel.write(dsl);
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

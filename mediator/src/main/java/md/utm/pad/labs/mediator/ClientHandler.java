package md.utm.pad.labs.mediator;

import md.utm.pad.labs.channel.ClientChannel;
import md.utm.pad.labs.channel.util.ChannelUtil;
import md.utm.pad.labs.client.NodeClient;
import md.utm.pad.labs.response.Response;
import md.utm.pad.labs.service.JsonService;
import md.utm.pad.labs.service.XmlService;
import org.apache.log4j.Logger;

import java.util.Optional;

/**
 * Created by anrosca on Nov, 2017
 */
public class ClientHandler implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class);

    private ClientChannel channel;
    private NodeClient nodeClient;
    private JsonService jsonService;
    private XmlService xmlService;

    private ClientHandler() {
    }

    @Override
    public void run() {
        try {
            while (true) {
                Optional<String> request = ChannelUtil.readRequest(channel);
                if (!request.isPresent())
                    break;
                if (!request.get().isEmpty()) {
                    Response response = nodeClient.submit(request.get());
                    channel.write(xmlService.toXml(response, Response.class));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error while handling client", e);
        }
    }

    public static ClientHandlerBuilder newBuilder() {
        return new ClientHandlerBuilder();
    }

    public static final class ClientHandlerBuilder {
        private ClientChannel channel;
        private NodeClient nodeClient;
        private JsonService jsonService;
        private XmlService xmlService;

        private ClientHandlerBuilder() {
        }

        public ClientHandlerBuilder setChannel(ClientChannel channel) {
            this.channel = channel;
            return this;
        }

        public ClientHandlerBuilder setNodeClient(NodeClient nodeClient) {
            this.nodeClient = nodeClient;
            return this;
        }

        public ClientHandlerBuilder setJsonService(JsonService jsonService) {
            this.jsonService = jsonService;
            return this;
        }

        public ClientHandlerBuilder setXmlService(XmlService xmlService) {
            this.xmlService = xmlService;
            return this;
        }

        public ClientHandler build() {
            ClientHandler clientHandler = new ClientHandler();
            clientHandler.channel = this.channel;
            clientHandler.jsonService = this.jsonService;
            clientHandler.nodeClient = this.nodeClient;
            clientHandler.xmlService = this.xmlService;
            return clientHandler;
        }
    }
}

package md.utm.pad.labs.mediator;

import javafx.util.Pair;
import md.utm.pad.labs.channel.ClientChannel;
import md.utm.pad.labs.channel.ResponseUtil;
import md.utm.pad.labs.channel.util.ChannelUtil;
import md.utm.pad.labs.response.Response;
import md.utm.pad.labs.service.JsonService;
import md.utm.pad.labs.service.XmlService;
import org.apache.log4j.Logger;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by anrosca on Nov, 2017
 */
public class ClientHandler implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class);
    private static final Pattern ACCEPT_HEADER_PATTERN = Pattern.compile("Accept: (?<mediaType>.+)");

    private ClientChannel channel;
    private JsonService jsonService;
    private XmlService xmlService;
    private MavenConnection mavenConnection;

    private ClientHandler() {
    }

    @Override
    public void run() {
        try {
            while (true) {
                Pair<String, String> requestData = ResponseUtil.readResponse(channel);
                if (requestData.getValue().isEmpty())
                    break;
                Response response = mavenConnection.submit(requestData.getValue());
                sendResponse(response, requestData.getKey());
            }
            mavenConnection.close();
        } catch (Exception e) {
            LOGGER.error("Error while handling client", e);
        }
    }

    private void sendResponse(Response response, String accepts) {
        Matcher matcher = ACCEPT_HEADER_PATTERN.matcher(accepts);
        if (matcher.find()) {
            String mediaType = matcher.group("mediaType");
            if (jsonService.getMediaType().equalsIgnoreCase(mediaType)) {
                channel.writeNoBreak(String.format("Content-Type: %s", jsonService.getMediaType()));
                channel.write(jsonService.toJson(response));
            } else if (xmlService.getMediaType().equalsIgnoreCase(mediaType)) {
                channel.writeNoBreak(String.format("Content-Type: %s", xmlService.getMediaType()));
                channel.write(xmlService.toXml(response, Response.class));
            } else
                throw new RuntimeException("Unknown media type: " + accepts);
        }
    }

    public static ClientHandlerBuilder newBuilder() {
        return new ClientHandlerBuilder();
    }

    public static final class ClientHandlerBuilder {
        private ClientChannel channel;
        private MavenConnection mavenConnection;
        private JsonService jsonService;
        private XmlService xmlService;

        private ClientHandlerBuilder() {
        }

        public ClientHandlerBuilder setChannel(ClientChannel channel) {
            this.channel = channel;
            return this;
        }

        public ClientHandlerBuilder setMavenConnection(MavenConnection mavenConnection) {
            this.mavenConnection = mavenConnection;
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
            clientHandler.mavenConnection = this.mavenConnection;
            clientHandler.xmlService = this.xmlService;
            return clientHandler;
        }
    }
}

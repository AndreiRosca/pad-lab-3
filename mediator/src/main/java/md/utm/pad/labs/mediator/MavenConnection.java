package md.utm.pad.labs.mediator;

import javafx.util.Pair;
import md.utm.pad.labs.channel.ClientChannel;
import md.utm.pad.labs.channel.ResponseUtil;
import md.utm.pad.labs.channel.SocketClientChannel;
import md.utm.pad.labs.channel.util.ChannelUtil;
import md.utm.pad.labs.client.MavenNode;
import md.utm.pad.labs.request.Request;
import md.utm.pad.labs.response.Response;
import md.utm.pad.labs.service.JsonService;
import md.utm.pad.labs.service.XmlService;
import org.apache.log4j.Logger;

import java.net.Socket;

/**
 * Created by anrosca on Nov, 2017
 */
public class MavenConnection implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(MavenConnection.class);

    private final MavenNode maven;
    private final JsonService jsonService;
    private final XmlService xmlService;
    private ClientChannel channel;

    public MavenConnection(MavenNode maven, JsonService jsonService, XmlService xmlService) {
        this.maven = maven;
        this.jsonService = jsonService;
        this.xmlService = xmlService;
        connectToMaven();
    }

    private void connectToMaven() {
        try {
            Socket socket = new Socket(maven.getAddress(), maven.getPort());
            channel = new SocketClientChannel(socket);
        } catch (Exception e) {
            LOGGER.error("Can't connect to Maven node", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        channel.close();
    }

    public Response submit(String dsl) {
        try {
            Request request = new Request(dsl);
            channel.write(jsonService.toJson(request));
            Pair<String, String> response = ResponseUtil.readResponse(channel);
            if (ResponseUtil.isResponseJson(response.getKey()))
                return jsonService.fromJson(response.getValue(), Response.class);
            else if (ResponseUtil.isResponseXml(response.getKey()))
                return xmlService.fromXml(response.getValue(), Response.class);
            throw new RuntimeException("Unexpected media type. Response: " + response);
        } catch (Exception e) {
            LOGGER.error("Can't send data to node", e);
            throw new RuntimeException(e);
        }
    }
}

package md.utm.pad.labs.client;

import javafx.util.Pair;
import md.utm.pad.labs.channel.ClientChannel;
import md.utm.pad.labs.channel.ResponseUtil;
import md.utm.pad.labs.channel.SocketClientChannel;
import md.utm.pad.labs.channel.util.ChannelUtil;
import md.utm.pad.labs.config.NodeClientConfiguration;
import md.utm.pad.labs.interogator.NodeInterogator;
import md.utm.pad.labs.request.Request;
import md.utm.pad.labs.response.DiscoverResponse;
import md.utm.pad.labs.response.Response;
import md.utm.pad.labs.service.JsonService;
import md.utm.pad.labs.service.XmlService;
import org.apache.log4j.Logger;

import java.net.Socket;
import java.util.Optional;

/**
 * Created by anrosca on Nov, 2017
 */
public class NodeClient implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(NodeClient.class);

    private final NodeInterogator interogator;
    private final JsonService jsonService;
    private final XmlService xmlService;
    private MavenNode mavenNode;
    private ClientChannel channel;

    public NodeClient(NodeClientConfiguration configuration, JsonService jsonService, XmlService xmlService) {
        this.xmlService = xmlService;
        interogator = new NodeInterogator(configuration, jsonService);
        this.jsonService = jsonService;
    }

    public void detectMavenNode() {
        interogator.interogateNodes();
        Optional<DiscoverResponse> maven = interogator.getNodes()
                .stream()
                .sorted()
                .findFirst();
        maven.ifPresent(r -> mavenNode =  new MavenNode(r.getNodeAddress(), r.getNodePort()));
        LOGGER.info("Maven node: " + mavenNode);
    }

    public void connectToMaven() {
        try {
            Socket socket = new Socket(mavenNode.getAddress(), mavenNode.getPort());
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

    public void getAll() {
        Request request = new Request("from Student where numberOfReportsToPresent > 1 order by age, name");
        channel.write(jsonService.toJson(request));
        Response response = jsonService.fromJson(ChannelUtil.readRequest(channel).get(), Response.class);
        LOGGER.info(response);
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

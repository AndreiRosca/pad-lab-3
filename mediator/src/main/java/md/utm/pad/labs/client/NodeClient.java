package md.utm.pad.labs.client;

import md.utm.pad.labs.channel.ClientChannel;
import md.utm.pad.labs.channel.SocketClientChannel;
import md.utm.pad.labs.config.NodeClientConfiguration;
import md.utm.pad.labs.interogator.NodeInterogator;
import md.utm.pad.labs.request.Request;
import md.utm.pad.labs.response.DiscoverResponse;
import md.utm.pad.labs.response.Response;
import md.utm.pad.labs.service.JsonService;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

/**
 * Created by anrosca on Nov, 2017
 */
public class NodeClient implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(NodeClient.class);

    private final NodeInterogator interogator;
    private final JsonService jsonService;
    private MavenNode mavenNode;
    private ClientChannel channel;

    public NodeClient(NodeClientConfiguration configuration, JsonService jsonService) {
        interogator = new NodeInterogator(configuration, jsonService);
        this.jsonService = jsonService;
    }

    public void detectMavenNode() {
        interogator.interogateNodes();
        Optional<DiscoverResponse> maven = interogator.getNodes().stream()
                .sorted()
                .findFirst();
        maven.ifPresent(r -> {
            mavenNode =  new MavenNode(r.getNodeAddress(), r.getNodePort());
        });
        LOGGER.info("Maven node: " + mavenNode);
    }

    public void connectToMaven() {
        try {
            Socket socket = new Socket(mavenNode.getAddress(), mavenNode.getPort());
            channel = new SocketClientChannel(socket);
        } catch (IOException e) {
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
        Response response = jsonService.fromJson(readJsonRequest(), Response.class);
        LOGGER.info(response);
    }

    public Response submit(String dsl) {
        Request request = new Request(dsl);
        channel.write(jsonService.toJson(request));
        return jsonService.fromJson(readJsonRequest(), Response.class);
    }

    private String readJsonRequest() {
        StringBuilder requestBuilder = new StringBuilder();
        String line;
        while ((line = channel.readLine()) != null && line.trim().length() > 0) {
            requestBuilder.append(line);
        }
        return requestBuilder.toString();
    }
}

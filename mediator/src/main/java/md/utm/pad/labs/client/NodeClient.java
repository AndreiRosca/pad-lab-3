package md.utm.pad.labs.client;

import md.utm.pad.labs.channel.ClientChannel;
import md.utm.pad.labs.config.NodeClientConfiguration;
import md.utm.pad.labs.interogator.NodeInterogator;
import md.utm.pad.labs.response.DiscoverResponse;
import md.utm.pad.labs.service.JsonService;
import md.utm.pad.labs.service.XmlService;
import org.apache.log4j.Logger;

import java.util.Optional;

/**
 * Created by anrosca on Nov, 2017
 */
public class NodeClient {
    private static final Logger LOGGER = Logger.getLogger(NodeClient.class);

    private final NodeInterogator interogator;
    private final JsonService jsonService;
    private final XmlService xmlService;
    private MavenNode mavenNode;

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

    public MavenNode getMavenNode() {
        return mavenNode;
    }
}

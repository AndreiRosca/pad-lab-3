package md.utm.pad.labs.node.config;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class NodeConfiguration {
    private final Properties properties;

    public NodeConfiguration(String propertiesFile) {
        try {
            properties = new Properties();
            properties.load(getClass().getResourceAsStream(propertiesFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getClientPort() {
        String clientPort = properties.getProperty("clientPort");
        return Integer.valueOf(clientPort);
    }

    public int getNodeDiscoverPort() {
        String nodePort = properties.getProperty("nodeDiscoverPort");
        return Integer.valueOf(nodePort);
    }

    public String getNodeDiscoverGroupAddress() {
        return properties.getProperty("nodeDiscoverGroupAddress");
    }

    public int getDatagramPacketSize() {
        String datagramPacketSize = properties.getProperty("datagramPacketSizeInBytes");
        return Integer.valueOf(datagramPacketSize);
    }

    public int getConsumerTcpPort() {
        String consumerTcpPort = properties.getProperty("consumerTcpPort");
        return Integer.valueOf(consumerTcpPort);
    }

    public List<URI> getPeerNodes() {
        String peerNodes = properties.getProperty("peerNodes");
        if (peerNodes == null)
            return Collections.emptyList();
        return Arrays.stream(peerNodes.split(", "))
                .map(peer -> {
                    try {
                        return new URI(peer);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }
}

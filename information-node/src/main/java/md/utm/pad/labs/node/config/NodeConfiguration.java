package md.utm.pad.labs.node.config;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
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
                .map(this::toUri)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private URI toUri(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDataFile() {
        return properties.getProperty("dataFile");
    }

    public int getPeerPort() {
        String port = properties.getProperty("peerPort");
        return Integer.valueOf(port);
    }
}

package md.utm.pad.labs.node.config;

import java.io.IOException;
import java.util.Properties;

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

    public int getNodePort() {
        String nodePort = properties.getProperty("nodePort");
        return Integer.valueOf(nodePort);
    }

    public String getNodeGroupAddress() {
        return properties.getProperty("nodeGroupAddress");
    }
}

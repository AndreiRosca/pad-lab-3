package md.utm.pad.labs.config;

import java.io.IOException;
import java.util.Properties;

public class ClientConfiguration {
    private final Properties properties;

    public ClientConfiguration(String propertiesFile) {
        properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream(propertiesFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getNodeMulticastAddress() {
        return properties.getProperty("nodeMulticastAddress");
    }

    public int getNodePort() {
        String nodePort = properties.getProperty("nodePort");
        return Integer.valueOf(nodePort);
    }

    public long getNodeResponseTimeout() {
        String responseTimeout = properties.getProperty("nodeResponseTimeoutInMilliseconds");
        return Long.valueOf(responseTimeout);
    }

    public int getClientPort() {
        String clientPort = properties.getProperty("clientPort");
        return Integer.valueOf(clientPort);
    }

    public int getDatagramPacketSize() {
        String datagramPacketSize = properties.getProperty("datagramPacketSizeInBytes");
        return Integer.valueOf(datagramPacketSize);
    }
}

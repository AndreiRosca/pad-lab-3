package md.utm.pad.labs.config;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by anrosca on Nov, 2017
 */
public class MediatorConfiguration {
    private final Properties properties;

    public MediatorConfiguration(String cfgFilePath) {
        properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream(cfgFilePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getMediatorPort() {
        String port = properties.getProperty("mediator.port");
        return Integer.valueOf(port);
    }

    public String getMediatorAddress() {
        return properties.getProperty("mediator.address");
    }
}

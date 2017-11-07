package md.utm.pad.labs.mediator;

import md.utm.pad.labs.channel.ClientChannel;
import md.utm.pad.labs.channel.SocketClientChannel;
import md.utm.pad.labs.client.NodeClient;
import md.utm.pad.labs.config.MediatorConfiguration;
import md.utm.pad.labs.config.NodeClientConfiguration;
import md.utm.pad.labs.service.JsonService;
import md.utm.pad.labs.service.XmlService;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by anrosca on Nov, 2017
 */
public class Mediator implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Mediator.class);
    private static final int MAX_THREADS = 4;

    private NodeClientConfiguration configuration;
    private JsonService jsonService;
    private XmlService xmlService;
    private MediatorConfiguration mediatorConfiguration;
    private NodeClient nodeClient;
    private ServerSocket serverSocket;
    private ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);

    private Mediator() {
    }

    public void init() {
        nodeClient = new NodeClient(configuration, jsonService, xmlService);
        nodeClient.detectMavenNode();
        nodeClient.connectToMaven();
        setUpServerSocket();
    }

    private void setUpServerSocket() {
        try {
            serverSocket = new ServerSocket(mediatorConfiguration.getMediatorPort());
            executorService.submit(this);
        } catch (IOException e) {
            LOGGER.error("Can't create the mediator server socket.", e);
            throw new RuntimeException(e);
        }
    }

    public void getAll() {
        nodeClient.getAll();
    }

    public void close() {
        try {
            nodeClient.close();
            closeSocket();
            executorService.shutdownNow();
        } catch (Exception e) {
            LOGGER.error("Error while closing the nodeClient", e);
        }
    }

    private void closeSocket() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.error("Closing the server socket.");
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientChannel channel = new SocketClientChannel(socket);
                executorService.submit(ClientHandler.newBuilder()
                        .setChannel(channel)
                        .setJsonService(jsonService)
                        .setNodeClient(nodeClient)
                        .setXmlService(xmlService)
                        .build());
            }
        } catch (Exception e) {
            if (e instanceof SocketException)
                LOGGER.info("Mediator socket closed.");
            else
                LOGGER.error("Error while serving the client.", e);
        }
    }

    public static MediatorBuilder newBuilder() {
        return new MediatorBuilder();
    }

    public static final class MediatorBuilder {
        private NodeClientConfiguration configuration;
        private JsonService jsonService;
        private XmlService xmlService;
        private MediatorConfiguration mediatorConfiguration;

        private MediatorBuilder() {
        }

        public MediatorBuilder setConfiguration(NodeClientConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        public MediatorBuilder setJsonService(JsonService jsonService) {
            this.jsonService = jsonService;
            return this;
        }

        public MediatorBuilder setXmlService(XmlService xmlService) {
            this.xmlService = xmlService;
            return this;
        }

        public MediatorBuilder setMediatorConfiguration(MediatorConfiguration mediatorConfiguration) {
            this.mediatorConfiguration = mediatorConfiguration;
            return this;
        }

        public Mediator build() {
            Mediator mediator = new Mediator();
            mediator.configuration = this.configuration;
            mediator.jsonService = this.jsonService;
            mediator.mediatorConfiguration = this.mediatorConfiguration;
            mediator.xmlService = this.xmlService;
            return mediator;
        }
    }
}

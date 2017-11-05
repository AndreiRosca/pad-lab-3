package md.utm.pad.labs.mediator;

import md.utm.pad.labs.channel.ClientChannel;
import md.utm.pad.labs.channel.SocketClientChannel;
import md.utm.pad.labs.channel.util.ChannelUtil;
import md.utm.pad.labs.client.NodeClient;
import md.utm.pad.labs.config.MediatorConfiguration;
import md.utm.pad.labs.config.NodeClientConfiguration;
import md.utm.pad.labs.service.JsonService;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by anrosca on Nov, 2017
 */
public class Mediator implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Mediator.class);
    private static final int MAX_THREADS = 4;

    private final NodeClientConfiguration configuration;
    private final JsonService jsonService;
    private final MediatorConfiguration mediatorConfiguration;
    private NodeClient nodeClient;
    private ServerSocket serverSocket;
    private final ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);

    public Mediator(NodeClientConfiguration configuration, JsonService jsonService, MediatorConfiguration mediatorConfiguration) {
        this.configuration = configuration;
        this.jsonService = jsonService;
        this.mediatorConfiguration = mediatorConfiguration;
    }

    public void init() {
        nodeClient = new NodeClient(configuration, jsonService);
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
            executorService.shutdownNow();
            closeSocket();
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
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                ClientChannel channel = new SocketClientChannel(socket);
                executorService.submit(new ClientHandler(channel, nodeClient, jsonService));
            } catch (IOException e) {
                LOGGER.error("Error while serving the client.", e);
            }
        }
    }
}

package md.utm.pad.labs.node;

import md.utm.pad.labs.handler.UdpClientHandler;
import md.utm.pad.labs.node.config.NodeConfiguration;
import md.utm.pad.labs.node.tcp.NodeConsumerServer;
import md.utm.pad.labs.request.DiscoverRequest;
import md.utm.pad.labs.response.DiscoverResponse;
import md.utm.pad.labs.service.JsonService;
import md.utm.pad.labs.service.RequestSerializer;
import md.utm.pad.labs.service.XmlService;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NodeServer implements Runnable {
    private static final int MAX_THREADS = 10;
    private static final Logger LOGGER = Logger.getLogger(NodeServer.class);

    private MulticastSocket socket;
    private final ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);
    private NodeConfiguration configuration;
    private JsonService jsonService;
    private XmlService xmlService;
    private UdpClientHandler clientHandler;
    private NodeConsumerServer nodeConsumerServer;
    private NodeConsumerServer peerServer;

    private NodeServer() {
    }

    private void init() {
        try {
            tryInit();
        } catch (IOException e) {
            LOGGER.error("Can't initialize the servers.", e);
            throw new RuntimeException(e);
        }
    }

    private void tryInit() throws IOException {
        this.nodeConsumerServer = NodeConsumerServer.newBuilder()
                .setExecutorService(executorService)
                .setSerializer(makeConfiguredSerializer())
                .setNodeContext(clientHandler.getNodeContext())
                .setPort(configuration.getConsumerTcpPort())
                .build();
        this.peerServer = NodeConsumerServer.newBuilder()
                .setExecutorService(executorService)
                .setSerializer(jsonService)
                .setNodeContext(clientHandler.getNodeContext())
                .setPort(configuration.getPeerPort())
                .build();
        this.socket = new MulticastSocket(configuration.getNodeDiscoverPort());
        this.socket.joinGroup(InetAddress.getByName(configuration.getNodeDiscoverGroupAddress()));
    }

    private RequestSerializer makeConfiguredSerializer() {
        return configuration.getClientResponseMediaType().contains("json") ? jsonService : xmlService;
    }

    public void start() {
        init();
        executorService.submit(this);
        executorService.submit(nodeConsumerServer);
        executorService.submit(peerServer);
    }

    public void stop() {
        try {
            tryStop();
        } catch (Exception e) {
            LOGGER.error("Can't stop", e);
        }
    }

    private void tryStop() throws IOException {
        socket.leaveGroup(InetAddress.getByName(configuration.getNodeDiscoverGroupAddress()));
        closeResource(socket);
        closeResource(nodeConsumerServer);
        closeResource(peerServer);
        executorService.shutdownNow();
    }

    private void closeResource(AutoCloseable resource) {
        try {
            resource.close();
        } catch (Exception e) {
            LOGGER.error("Can't close the socket.", e);
        }
    }

    @Override
    public void run() {
        try {
            serveClients();
        } catch (Exception e) {
            if (e instanceof SocketException)
                LOGGER.info("Node socket closed");
            else
                LOGGER.error("Error while serving clients.", e);
        }
    }

    private void serveClients() throws IOException {
        while (true) {
            DatagramPacket packet = makeDatagramPacket();
            socket.receive(packet);
            DiscoverRequest request = jsonService.fromJson(new String(packet.getData()), DiscoverRequest.class);
            Optional<DiscoverResponse> response = clientHandler.handleRequest(request);
            response.ifPresent((r) -> sendResponse(packet.getAddress(), packet.getPort(), r));
        }
    }

    private void sendResponse(InetAddress address, int port, DiscoverResponse response) {
        try {
            trySendResponse(address, port, response);
        } catch (Exception e) {
            LOGGER.error("Can't send the discover response", e);
        }
    }

    private void trySendResponse(InetAddress address, int port, DiscoverResponse response) throws IOException {
        DatagramPacket responsePacket = makeDatagramPacket();
        responsePacket.setAddress(address);
        responsePacket.setPort(port);
        responsePacket.setData(jsonService.toJson(response).getBytes());
        socket.send(responsePacket);
    }

    private DatagramPacket makeDatagramPacket() {
        byte[] buffer = new byte[configuration.getDatagramPacketSize()];
        return new DatagramPacket(buffer, buffer.length);
    }

    public static NodeServerBuilder newBuilder() {
        return new NodeServerBuilder();
    }

    public static final class NodeServerBuilder {
        private NodeServer server = new NodeServer();

        private NodeServerBuilder() {
        }

        public NodeServerBuilder setConfiguration(NodeConfiguration configuration) {
            server.configuration = configuration;
            return this;
        }

        public NodeServerBuilder setJsonService(JsonService jsonService) {
            server.jsonService = jsonService;
            return this;
        }

        public NodeServerBuilder setXmlService(XmlService xmlService) {
            server.xmlService = xmlService;
            return this;
        }

        public NodeServerBuilder setClientHandler(UdpClientHandler clientHandler) {
            server.clientHandler = clientHandler;
            return this;
        }

        public NodeServer build() {
            return server;
        }
    }
}

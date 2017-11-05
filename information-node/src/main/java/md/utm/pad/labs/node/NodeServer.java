package md.utm.pad.labs.node;

import md.utm.pad.labs.handler.UdpClientHandler;
import md.utm.pad.labs.node.config.NodeConfiguration;
import md.utm.pad.labs.node.tcp.NodeConsumerServer;
import md.utm.pad.labs.request.DiscoverRequest;
import md.utm.pad.labs.response.DiscoverResponse;
import md.utm.pad.labs.service.JsonService;
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

    private final MulticastSocket socket;
    private final ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);
    private final NodeConfiguration configuration;
    private final JsonService jsonService;
    private final UdpClientHandler clientHandler;
    private final NodeConsumerServer nodeConsumerServer;
    private final NodeConsumerServer peerServer;

    public NodeServer(NodeConfiguration configuration, JsonService jsonService, UdpClientHandler clientHandler) {
        try {
            this.clientHandler = clientHandler;
            this.jsonService = jsonService;
            this.configuration = configuration;
            this.nodeConsumerServer = NodeConsumerServer.newBuilder()
                    .setExecutorService(executorService)
                    .setJsonService(jsonService)
                    .setNodeContext(clientHandler.getNodeContext())
                    .setPort(configuration.getConsumerTcpPort())
                    .build();
            this.peerServer = NodeConsumerServer.newBuilder()
                    .setExecutorService(executorService)
                    .setJsonService(jsonService)
                    .setNodeContext(clientHandler.getNodeContext())
                    .setPort(configuration.getPeerPort())
                    .build();
            this.socket = new MulticastSocket(configuration.getNodeDiscoverPort());
            this.socket.joinGroup(InetAddress.getByName(configuration.getNodeDiscoverGroupAddress()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        executorService.submit(this);
        executorService.submit(nodeConsumerServer);
        executorService.submit(peerServer);
    }

    public void stop() {
        try {
            tryStop();
        } catch (Exception e) {
        }
    }

    private void tryStop() throws IOException {
        socket.leaveGroup(InetAddress.getByName(configuration.getNodeDiscoverGroupAddress()));
        executorService.shutdownNow();
        closeResource(socket);
        closeResource(nodeConsumerServer);
        closeResource(peerServer);
    }

    private void closeResource(AutoCloseable socket) {
        try {
            socket.close();
        } catch (Exception e) {
            LOGGER.error("Can't close the socket.", e);
        }
    }

    @Override
    public void run() {
        try {
            serveClients();
        } catch (IOException e) {
            if (e instanceof SocketException)
                LOGGER.info("Node socket closed");
            else if (e instanceof SocketException)
                LOGGER.error("Socket closed.");
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
        } catch (IOException e) {
            throw new RuntimeException(e);
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
}

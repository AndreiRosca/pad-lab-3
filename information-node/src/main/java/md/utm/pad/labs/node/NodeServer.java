package md.utm.pad.labs.node;

import md.utm.pad.labs.node.config.NodeConfiguration;
import md.utm.pad.labs.request.RequestType;
import md.utm.pad.labs.response.Response;
import md.utm.pad.labs.response.ResponseType;
import md.utm.pad.labs.service.JsonService;
import md.utm.pad.labs.request.Request;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NodeServer implements Runnable {
    private static final int MAX_THREADS = 3;

    private final MulticastSocket socket;
    private final ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);
    private final NodeConfiguration configuration;
    private final JsonService jsonService;

    public NodeServer(NodeConfiguration configuration, JsonService jsonService) {
        try {
            this.jsonService = jsonService;
            this.configuration = configuration;
            this.socket = new MulticastSocket(configuration.getNodePort());
            this.socket.joinGroup(InetAddress.getByName(configuration.getNodeGroupAddress()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        executorService.submit(this);
    }

    public void stop() {
        try {
            tryStop();
        } catch (Exception e) {
        }
    }

    private void tryStop() throws IOException {
        socket.leaveGroup(InetAddress.getByName(configuration.getNodeGroupAddress()));
        executorService.shutdownNow();
        socket.close();
    }

    @Override
    public void run() {
        try {
            serveClients();
        } catch (IOException e) {
            if (e instanceof SocketException)
                System.out.println("Node socket closed");
            else
                e.printStackTrace();
        }
    }

    private void serveClients() throws IOException {
        while (true) {
            DatagramPacket packet = makeDatagramPacket();
            socket.receive(packet);
            Request request = jsonService.fromJson(new String(packet.getData()), Request.class);
            if (request.getType().equalsIgnoreCase(RequestType.PRESENT.toString())) {
                Response response = new Response(ResponseType.PRESENT_RESPONSE, UUID.randomUUID().toString());
                sendResponse(packet, response);
                System.out.println("Response sent");
            } else {
                System.out.println("Unknown request: " + request);
            }
        }
    }

    private void sendResponse(DatagramPacket packet, Response response) throws IOException {
        DatagramPacket responsePacket = makeDatagramPacket();
        responsePacket.setAddress(packet.getAddress());
        responsePacket.setPort(configuration.getClientPort());
        responsePacket.setData(jsonService.toJson(response).getBytes());
        socket.send(responsePacket);
    }

    private DatagramPacket makeDatagramPacket() {
        byte[] buffer = new byte[configuration.getDatagramPacketSize()];
        return new DatagramPacket(buffer, buffer.length);
    }
}

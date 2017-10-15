package md.utm.pad.labs.node;

import md.utm.pad.labs.node.config.NodeConfiguration;

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

    public NodeServer(NodeConfiguration configuration) {
        try {
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

            String data = new String(packet.getData());
            System.out.println("Got: " + data);

            sendResponse(packet);

            System.out.println("Response sent");
        }
    }

    private void sendResponse(DatagramPacket packet) throws IOException {
        DatagramPacket responsePacket = makeDatagramPacket();
        responsePacket.setAddress(packet.getAddress());
        responsePacket.setPort(configuration.getClientPort());
        responsePacket.setData(("{ 'type': 'presentResponse', 'data': '" + UUID.randomUUID() +  "' }").getBytes());
        socket.send(responsePacket);
    }

    private DatagramPacket makeDatagramPacket() {
        byte[] buffer = new byte[configuration.getDatagramPacketSize()];
        return new DatagramPacket(buffer, buffer.length);
    }
}

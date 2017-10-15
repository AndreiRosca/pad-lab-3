package md.utm.pad.labs.interogator;

import md.utm.pad.labs.config.ClientConfiguration;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NodeInterogator implements Runnable {
    private final ClientConfiguration configuration;
    private final Set<String> nodes = new HashSet<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private DatagramSocket socket;

    public NodeInterogator(ClientConfiguration configuration) {
        this.configuration = configuration;
    }

    public void interogateNodes() {
        try {
            socket = new DatagramSocket(configuration.getClientPort());
            InetAddress nodeGroup = InetAddress.getByName(configuration.getNodeMulticastAddress());
            byte[] buffer = "{ 'type': 'presentRequest' }".getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            packet.setAddress(nodeGroup);
            packet.setPort(configuration.getNodePort());
            socket.send(packet);
            awaitNodeResponses();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void awaitNodeResponses() {
        try {
            tryAwaitNodeResponses();
        } catch (InterruptedException e) {
            e.printStackTrace(System.out);
        }
    }

    private void tryAwaitNodeResponses() throws InterruptedException {
        executorService.submit(this);
        TimeUnit.MILLISECONDS.sleep(configuration.getNodeResponseTimeout());
        executorService.shutdownNow();
        socket.close();
    }

    public void run() {
        try {
            tryRun();
        } catch (IOException e) {
            System.out.println("Interogator socket closed");
        }
    }

    private void tryRun() throws IOException {
        long startTime = System.currentTimeMillis();
        while (!isTimeout(startTime)) {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            nodes.add(new String(packet.getData()));

            System.out.println("Got response");
        }
    }

    private boolean isTimeout(long startTime) {
        return System.currentTimeMillis() - startTime > configuration.getNodeResponseTimeout();
    }

    public Set<String> getNodes() {
        return nodes;
    }
}

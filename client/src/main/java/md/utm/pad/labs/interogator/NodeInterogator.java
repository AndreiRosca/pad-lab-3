package md.utm.pad.labs.interogator;

import md.utm.pad.labs.config.ClientConfiguration;

import java.io.IOException;
import java.net.*;
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
            tryInterogateNodes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void tryInterogateNodes() throws IOException {
        socket = new DatagramSocket(configuration.getClientPort());
        DatagramPacket packet = makeRequestDatagram();
        socket.send(packet);
        awaitNodeResponses();
    }

    private DatagramPacket makeRequestDatagram() throws UnknownHostException {
        byte[] buffer = "{ 'type': 'presentRequest' }".getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        packet.setAddress(InetAddress.getByName(configuration.getNodeMulticastAddress()));
        packet.setPort(configuration.getNodePort());
        return packet;
    }

    private void awaitNodeResponses() {
        try {
            tryAwaitNodeResponses();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
            DatagramPacket packet = makeDatagramPacket();
            socket.receive(packet);
            nodes.add(new String(packet.getData()));

            System.out.println("Got response");
        }
    }

    private DatagramPacket makeDatagramPacket() {
        byte[] buffer = new byte[configuration.getDatagramPacketSize()];
        return new DatagramPacket(buffer, buffer.length);
    }

    private boolean isTimeout(long startTime) {
        return System.currentTimeMillis() - startTime > configuration.getNodeResponseTimeout();
    }

    public Set<String> getNodes() {
        return nodes;
    }
}

package md.utm.pad.labs.interogator;

import md.utm.pad.labs.config.ClientConfiguration;
import md.utm.pad.labs.request.Request;
import md.utm.pad.labs.request.RequestType;
import md.utm.pad.labs.response.Response;
import md.utm.pad.labs.service.JsonService;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NodeInterogator implements Runnable {
    private final ClientConfiguration configuration;
    private final List<Response> nodes = new ArrayList<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final JsonService jsonService;
    private DatagramSocket socket;

    public NodeInterogator(ClientConfiguration configuration, JsonService jsonService) {
        this.configuration = configuration;
        this.jsonService = jsonService;
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
        Request request = new Request(RequestType.PRESENT);
        byte[] buffer = jsonService.toJson(request).getBytes();
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
            addNodeResponse(packet.getData());
        }
    }

    private void addNodeResponse(byte[] data) {
        Response response = jsonService.fromJson(new String(data), Response.class);
        nodes.add(response);
    }

    private DatagramPacket makeDatagramPacket() {
        byte[] buffer = new byte[configuration.getDatagramPacketSize()];
        return new DatagramPacket(buffer, buffer.length);
    }

    private boolean isTimeout(long startTime) {
        return System.currentTimeMillis() - startTime > configuration.getNodeResponseTimeout();
    }

    public List<Response> getNodes() {
        return nodes;
    }
}

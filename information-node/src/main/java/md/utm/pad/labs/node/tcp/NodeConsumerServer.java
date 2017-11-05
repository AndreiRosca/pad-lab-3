package md.utm.pad.labs.node.tcp;

import md.utm.pad.labs.channel.ClientChannel;
import md.utm.pad.labs.channel.SocketClientChannel;
import md.utm.pad.labs.node.config.NodeConfiguration;
import md.utm.pad.labs.service.JsonService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class NodeConsumerServer implements Runnable, AutoCloseable {
    private final ExecutorService executorService;
    private final JsonService jsonService;
    private final NodeConfiguration configuration;
    private ServerSocket serverSocket;

    public NodeConsumerServer(ExecutorService executorService, JsonService jsonService, NodeConfiguration configuration) {
        this.executorService = executorService;
        this.jsonService = jsonService;
        this.configuration = configuration;
    }

    public void run() {
        try {
            setUpServer();
            serveClients();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpServer() {
        try {
            serverSocket = new ServerSocket(configuration.getConsumerTcpPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void serveClients() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            executorService.submit(new NodeConsumerHandler(new SocketClientChannel(socket)));
        }
    }

    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
        }
    }
}

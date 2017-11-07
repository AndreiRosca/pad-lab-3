package md.utm.pad.labs.node.tcp;

import md.utm.pad.labs.channel.SocketClientChannel;
import md.utm.pad.labs.node.context.NodeContext;
import md.utm.pad.labs.node.tcp.handler.NodeRequestHandler;
import md.utm.pad.labs.service.JsonService;
import md.utm.pad.labs.service.RequestSerializer;
import md.utm.pad.labs.service.XmlService;
import md.utm.pad.labs.service.impl.JacksonJsonService;
import md.utm.pad.labs.service.impl.JaxbXmlService;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;

public class NodeConsumerServer implements Runnable, AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(NodeConsumerServer.class);

    private ExecutorService executorService;
    private RequestSerializer serializer;
    private NodeContext nodeContext;
    private ServerSocket serverSocket;
    private int port;

    private NodeConsumerServer() {
    }

    public void run() {
        try {
            setUpServer();
            serveClients();
        } catch (Exception e) {
            if (e instanceof SocketException)
                LOGGER.info("Closing the socket");
            else
                LOGGER.error("Error while serving clients.", e);
        }
    }

    private void setUpServer() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (Exception e) {
            LOGGER.error("Can't set up the server", e);
            throw new RuntimeException(e);
        }
    }

    private void serveClients() throws IOException {
        while (true) {
            SocketClientChannel channel = new SocketClientChannel(serverSocket.accept());
            executorService.submit(NodeConsumerHandler.newBuilder()
                    .setClientChannel(channel)
                    .setSerializer(serializer)
                    .setRequestHandler(new NodeRequestHandler(nodeContext, new JacksonJsonService(), new JaxbXmlService()))
                    .build());
        }
    }

    public void close() {
        try {
            serverSocket.close();
        } catch (Exception e) {
            LOGGER.info("Closing the server socket.");
        }
    }

    public static NodeConsumerServerBuilder newBuilder() {
        return new NodeConsumerServerBuilder();
    }

    public static final class NodeConsumerServerBuilder {
        private NodeConsumerServer nodeConsumerServer = new NodeConsumerServer();

        private NodeConsumerServerBuilder() {
        }

        public NodeConsumerServerBuilder setExecutorService(ExecutorService executorService) {
            nodeConsumerServer.executorService = executorService;
            return this;
        }

        public NodeConsumerServerBuilder setSerializer(RequestSerializer serializer) {
            nodeConsumerServer.serializer = serializer;
            return this;
        }

        public NodeConsumerServerBuilder setNodeContext(NodeContext nodeContext) {
            nodeConsumerServer.nodeContext = nodeContext;
            return this;
        }

        public NodeConsumerServerBuilder setPort(int port) {
            nodeConsumerServer.port = port;
            return this;
        }

        public NodeConsumerServer build() {
            return nodeConsumerServer;
        }
    }
}

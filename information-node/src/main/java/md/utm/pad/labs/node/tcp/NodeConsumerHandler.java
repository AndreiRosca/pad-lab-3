package md.utm.pad.labs.node.tcp;

import md.utm.pad.labs.channel.SocketClientChannel;
import md.utm.pad.labs.channel.util.ChannelUtil;
import md.utm.pad.labs.node.tcp.handler.NodeRequestHandler;
import md.utm.pad.labs.request.Request;
import md.utm.pad.labs.response.Response;
import md.utm.pad.labs.service.JsonService;
import md.utm.pad.labs.service.RequestSerializer;

import java.util.Optional;

public class NodeConsumerHandler implements Runnable {

    private SocketClientChannel clientChannel;
    private RequestSerializer serializer;
    private NodeRequestHandler requestHandler;

    private NodeConsumerHandler() {
    }

    public void run() {
        while (true) {
            Optional<String> jsonRequest = ChannelUtil.readRequest(clientChannel);
            if (!jsonRequest.isPresent())
                break;
            if (!jsonRequest.get().isEmpty()) {
                Request request = serializer.deserialize(jsonRequest.get(), Request.class);
                Optional<Response> response = requestHandler.handleRequest(request);
                response.ifPresent(this::sendResponse);
            }
        }
    }

    private void sendResponse(Response response) {
        clientChannel.writeNoBreak(String.format("Content-Type: %s", serializer.getMediaType()));
        clientChannel.write(serializer.serialize(response, Response.class));
    }

    public static NodeConsumerHandlerBuilder newBuilder() {
        return new NodeConsumerHandlerBuilder();
    }

    public static final class NodeConsumerHandlerBuilder {
        private SocketClientChannel clientChannel;
        private RequestSerializer serializer;
        private NodeRequestHandler requestHandler;

        private NodeConsumerHandlerBuilder() {
        }

        public NodeConsumerHandlerBuilder setClientChannel(SocketClientChannel clientChannel) {
            this.clientChannel = clientChannel;
            return this;
        }

        public NodeConsumerHandlerBuilder setSerializer(RequestSerializer serializer) {
            this.serializer = serializer;
            return this;
        }

        public NodeConsumerHandlerBuilder setRequestHandler(NodeRequestHandler requestHandler) {
            this.requestHandler = requestHandler;
            return this;
        }

        public NodeConsumerHandler build() {
            NodeConsumerHandler handler = new NodeConsumerHandler();
            handler.clientChannel = clientChannel;
            handler.serializer = serializer;
            handler.requestHandler = requestHandler;
            return handler;
        }
    }
}

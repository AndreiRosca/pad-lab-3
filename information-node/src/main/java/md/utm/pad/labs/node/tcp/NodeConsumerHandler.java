package md.utm.pad.labs.node.tcp;

import md.utm.pad.labs.channel.SocketClientChannel;
import md.utm.pad.labs.channel.util.ChannelUtil;
import md.utm.pad.labs.node.tcp.handler.NodeRequestHandler;
import md.utm.pad.labs.request.Request;
import md.utm.pad.labs.response.Response;
import md.utm.pad.labs.service.JsonService;

import java.util.Optional;

public class NodeConsumerHandler implements Runnable {

    private final SocketClientChannel clientChannel;
    private final JsonService jsonService;
    private final NodeRequestHandler requestHandler;

    public NodeConsumerHandler(SocketClientChannel clientChannel, JsonService jsonService, NodeRequestHandler requestHandler) {
        this.clientChannel = clientChannel;
        this.jsonService = jsonService;
        this.requestHandler = requestHandler;
    }

    public void run() {
        while (true) {
            Optional<String> jsonRequest = ChannelUtil.readRequest(clientChannel);
            if (!jsonRequest.isPresent())
                break;
            if (!jsonRequest.get().isEmpty()) {
                Request request = jsonService.fromJson(jsonRequest.get(), Request.class);
                Optional<Response> response = requestHandler.handleRequest(request);
                response.ifPresent(this::sendResponse);
            }
        }
    }

    private void sendResponse(Response response) {
        clientChannel.write(jsonService.toJson(response));
    }
}

package md.utm.pad.labs.handler;

import md.utm.pad.labs.node.config.NodeConfiguration;
import md.utm.pad.labs.node.context.NodeContext;
import md.utm.pad.labs.request.Request;
import md.utm.pad.labs.request.RequestType;
import md.utm.pad.labs.response.Response;
import md.utm.pad.labs.response.ResponseType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class DefaultClientHandler implements UdpClientHandler {
    private final Map<String, Function<Request, Optional<Response>>> handlers = new HashMap<>();
    private final NodeContext nodeContext;

    public DefaultClientHandler(NodeContext nodeContext) {
        this.nodeContext = nodeContext;
        handlers.put(RequestType.PRESENT.toString(), this::handlePresentRequest);
    }

    @Override
    public Optional<Response> handleRequest(Request request) {
        Function<Request, Optional<Response>> handler = handlers.getOrDefault(request.getType(), (r) -> Optional.empty());
        return handler.apply(request);
    }

    private Optional<Response> handlePresentRequest(Request request) {
        Response response = new Response(ResponseType.PRESENT_RESPONSE, nodeContext.getCollectionSize(),
                nodeContext.getNumberOfConnections());
        return Optional.of(response);
    }
}

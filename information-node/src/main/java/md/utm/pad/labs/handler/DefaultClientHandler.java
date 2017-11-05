package md.utm.pad.labs.handler;

import md.utm.pad.labs.node.context.NodeContext;
import md.utm.pad.labs.request.DiscoverRequest;
import md.utm.pad.labs.request.DiscoverRequestType;
import md.utm.pad.labs.response.DiscoverResponse;
import md.utm.pad.labs.response.DiscoverResponseType;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class DefaultClientHandler implements UdpClientHandler {
    private final Map<String, Function<DiscoverRequest, Optional<DiscoverResponse>>> handlers = new HashMap<>();
    private final NodeContext nodeContext;

    public DefaultClientHandler(NodeContext nodeContext) {
        this.nodeContext = nodeContext;
        handlers.put(DiscoverRequestType.PRESENT.toString(), this::handlePresentRequest);
    }

    @Override
    public Optional<DiscoverResponse> handleRequest(DiscoverRequest request) {
        Function<DiscoverRequest, Optional<DiscoverResponse>> handler = handlers.getOrDefault(request.getType(), (r) -> Optional.empty());
        return handler.apply(request);
    }

    @Override
    public NodeContext getNodeContext() {
        return nodeContext;
    }

    private Optional<DiscoverResponse> handlePresentRequest(DiscoverRequest request) {
        try {
            DiscoverResponse response = new DiscoverResponse(DiscoverResponseType.PRESENT_RESPONSE, nodeContext.getCollectionSize(),
                    nodeContext.getNumberOfConnections());
            response.setNodeAddress(InetAddress.getLocalHost().getHostAddress());
            response.setNodePort(nodeContext.getNodePort());
            return Optional.of(response);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}

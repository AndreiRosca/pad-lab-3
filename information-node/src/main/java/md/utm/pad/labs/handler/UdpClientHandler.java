package md.utm.pad.labs.handler;

import md.utm.pad.labs.node.context.NodeContext;
import md.utm.pad.labs.request.DiscoverRequest;
import md.utm.pad.labs.response.DiscoverResponse;

import java.util.Optional;

public interface UdpClientHandler {
    Optional<DiscoverResponse> handleRequest(DiscoverRequest request);

    NodeContext getNodeContext();
}

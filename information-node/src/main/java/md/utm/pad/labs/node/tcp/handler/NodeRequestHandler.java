package md.utm.pad.labs.node.tcp.handler;

import md.utm.pad.labs.node.context.NodeContext;
import md.utm.pad.labs.request.Request;
import md.utm.pad.labs.request.RequestType;
import md.utm.pad.labs.response.Response;
import md.utm.pad.labs.response.ResponseType;

import java.util.Optional;

/**
 * Created by anrosca on Nov, 2017
 */
public class NodeRequestHandler {
    private final NodeContext nodeContext;

    public NodeRequestHandler(NodeContext nodeContext) {
        this.nodeContext = nodeContext;
    }

    public Optional<Response> handleRequest(Request request) {
        System.out.println("Got request: " + request);
        if (request.getType().equalsIgnoreCase(RequestType.GET_ALL.toString())) {
            return Optional.of(new Response(ResponseType.GET_ALL, nodeContext.getAll()));
        }
        return Optional.empty();
    }
}

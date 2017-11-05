package md.utm.pad.labs.node.tcp.handler;

import md.utm.pad.labs.channel.ClientChannel;
import md.utm.pad.labs.channel.SocketClientChannel;
import md.utm.pad.labs.node.context.NodeContext;
import md.utm.pad.labs.request.Request;
import md.utm.pad.labs.request.RequestType;
import md.utm.pad.labs.response.Response;
import md.utm.pad.labs.response.ResponseType;
import md.utm.pad.labs.service.JsonService;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by anrosca on Nov, 2017
 */
public class NodeRequestHandler {
    private static final Logger LOGGER = Logger.getLogger(NodeRequestHandler.class);
    private final Map<String, Function<Request, Optional<Response>>> requestHandlers = new HashMap<>();

    private final NodeContext nodeContext;
    private final JsonService jsonService;

    public NodeRequestHandler(NodeContext nodeContext, JsonService jsonService) {
        this.nodeContext = nodeContext;
        this.jsonService = jsonService;
        setUpRequestHandlers();
    }

    private void setUpRequestHandlers() {
        requestHandlers.put(RequestType.GET_ALL.toString(), this::handleGetAllRequest);
    }

    private Optional<Response> handleGetAllRequest(Request request) {
        Response nodeResponse = new Response(ResponseType.GET_ALL, nodeContext.getAll());
        return Optional.of(nodeResponse);
    }

    public Optional<Response> handleRequest(Request request) {
        LOGGER.info("Got request: " + request);
        List<Response> responses = sendRequestToPeersAndAwaitResponse(request);
        Function<Request, Optional<Response>> handler = requestHandlers.get(request.getType());
        return mergeResponses(responses, handler.apply(request));
    }

    private Optional<Response> mergeResponses(List<Response> responses, Optional<Response> currentNodeResponse) {
        Response response = new Response();
        currentNodeResponse.ifPresent(r -> response.getResponseData().addAll(r.getResponseData()));
        response.getResponseData().addAll(
                responses.stream()
                        .flatMap(r -> r.getResponseData().stream())
                        .collect(Collectors.toList()));
        return Optional.of(response);
    }

    private List<Response> trySendRequestToPeersAndAwaitResponse(Request request) throws IOException {
        List<Response> responses = new ArrayList<>();
        List<URI> peerNodes = nodeContext.getPeerNodes();
        for (URI peer : peerNodes) {
            try {
                trySendToPeer(request, responses, peer);
            } catch (Exception e) {
                LOGGER.info("Peer: " + peer + " is down.");
            }
        }
        return responses;
    }

    private void trySendToPeer(Request request, List<Response> responses, URI peer) throws IOException {
        ClientChannel channel = new SocketClientChannel(new Socket(peer.getHost(), peer.getPort()));
        channel.write(jsonService.toJson(request));
        Response response = jsonService.fromJson(readJsonRequest(channel), Response.class);
        responses.add(response);
        channel.close();
    }

    private String readJsonRequest(ClientChannel channel) {
        StringBuilder requestBuilder = new StringBuilder();
        String line;
        while ((line = channel.readLine()) != null && line.trim().length() > 0) {
            requestBuilder.append(line);
        }
        return requestBuilder.toString();
    }

    private List<Response> sendRequestToPeersAndAwaitResponse(Request request) {
        try {
            return trySendRequestToPeersAndAwaitResponse(request);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

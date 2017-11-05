package md.utm.pad.labs.node.tcp.handler;

import md.utm.pad.labs.channel.ClientChannel;
import md.utm.pad.labs.channel.SocketClientChannel;
import md.utm.pad.labs.node.context.NodeContext;
import md.utm.pad.labs.request.Request;
import md.utm.pad.labs.request.RequestType;
import md.utm.pad.labs.response.Response;
import md.utm.pad.labs.response.ResponseType;
import md.utm.pad.labs.service.JsonService;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by anrosca on Nov, 2017
 */
public class NodeRequestHandler {
    private final NodeContext nodeContext;
    private final JsonService jsonService;

    public NodeRequestHandler(NodeContext nodeContext, JsonService jsonService) {
        this.nodeContext = nodeContext;
        this.jsonService = jsonService;
    }

    public Optional<Response> handleRequest(Request request) {
        System.out.println("Got request: " + request);
        List<Response> responses = sendRequestToPeersAndAwaitResponse(request);
        if (request.getType().equalsIgnoreCase(RequestType.GET_ALL.toString())) {
            Response currentNodeResponse = new Response(ResponseType.GET_ALL, nodeContext.getAll());
            Response response = mergeResponses(responses, currentNodeResponse);
            return Optional.of(response);
        }
        return Optional.empty();
    }

    private Response mergeResponses(List<Response> responses, Response currentNodeResponse) {
        Response response = new Response(currentNodeResponse.getType());
        response.getResponseData().addAll(currentNodeResponse.getResponseData());
        response.getResponseData().addAll(responses.stream()
            .flatMap(r -> r.getResponseData().stream())
            .collect(Collectors.toList()));
        return response;
    }

    private List<Response> trySendRequestToPeersAndAwaitResponse(Request request) throws IOException {
        List<Response> responses = new ArrayList<>();
        List<URI> peerNodes = nodeContext.getPeerNodes();
        for (URI peer : peerNodes) {
            try {
                trySendToPeer(request, responses, peer);
            } catch (Exception e) {
                System.out.println("Peer: " + peer + " is down.");
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

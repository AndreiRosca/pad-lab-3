package md.utm.pad.labs.node.tcp.handler;

import md.utm.pad.labs.channel.ClientChannel;
import md.utm.pad.labs.channel.SocketClientChannel;
import md.utm.pad.labs.domain.Student;
import md.utm.pad.labs.node.context.NodeContext;
import md.utm.pad.labs.node.dsl.DslParser;
import md.utm.pad.labs.request.Request;
import md.utm.pad.labs.response.Response;
import md.utm.pad.labs.service.JsonService;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by anrosca on Nov, 2017
 */
public class NodeRequestHandler {
    private static final Logger LOGGER = Logger.getLogger(NodeRequestHandler.class);

    private final NodeContext nodeContext;
    private final JsonService jsonService;

    public NodeRequestHandler(NodeContext nodeContext, JsonService jsonService) {
        this.nodeContext = nodeContext;
        this.jsonService = jsonService;
    }

    public Optional<Response> handleRequest(Request request) {
        LOGGER.info("Got request: " + request);
        List<Response> responses = sendRequestToPeersAndAwaitResponse(request);
        DslParser parser = new DslParser();
        List<Student> resultData = parser.execute(request.getRequest(), prepareDataset());
        return mergeResponses(responses, new Response(request.getRequest(), resultData));
    }

    private Map<String, List<Student>> prepareDataset() {
        return Collections.singletonMap(Student.class.getSimpleName(), nodeContext.getAll());
    }

    private Optional<Response> mergeResponses(List<Response> responses, Response currentNodeResponse) {
        Response response = new Response();
        response.getResponseData().addAll(currentNodeResponse.getResponseData());
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

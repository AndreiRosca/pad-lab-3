package md.utm.pad.labs.node.tcp.handler;

import md.utm.pad.labs.channel.ClientChannel;
import md.utm.pad.labs.channel.SocketClientChannel;
import md.utm.pad.labs.channel.util.ChannelUtil;
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
        List<Student> resultData = parser.execute(request.getDslRequest(), prepareDataset());
        Response response = mergeResponses(responses, new Response(resultData));
        return Optional.of(postProcessResponse(request, response));
    }

    private Response postProcessResponse(Request request, Response response) {
        Map<String, List<Student>> dataSet = Collections.singletonMap(Student.class.getSimpleName(), response.getResponseData());
        DslParser parser = new DslParser();
        List<Student> resultData = parser.execute(request.getDslRequest(), dataSet);
        response.setResponseData(resultData);
        return response;
    }

    private Map<String, List<Student>> prepareDataset() {
        return Collections.singletonMap(Student.class.getSimpleName(), nodeContext.getAll());
    }

    private Response mergeResponses(List<Response> responses, Response currentNodeResponse) {
        Response response = new Response();
        response.getResponseData().addAll(currentNodeResponse.getResponseData());
        response.getResponseData().addAll(
                responses.stream()
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
                LOGGER.info("Peer: " + peer + " is down.");
            }
        }
        return responses;
    }

    private void trySendToPeer(Request request, List<Response> responses, URI peer) throws IOException {
        ClientChannel channel = new SocketClientChannel(new Socket(peer.getHost(), peer.getPort()));
        channel.write(jsonService.toJson(request));
        Response response = jsonService.fromJson(ChannelUtil.readRequest(channel).get(), Response.class);
        responses.add(response);
        channel.close();
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

package md.utm.pad.labs.handler;

import md.utm.pad.labs.request.Request;
import md.utm.pad.labs.response.Response;

import java.util.Optional;

public interface UdpClientHandler {
    Optional<Response> handleRequest(Request request);
}

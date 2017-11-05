package md.utm.pad.labs.mediator;

import md.utm.pad.labs.channel.ClientChannel;
import md.utm.pad.labs.channel.util.ChannelUtil;
import md.utm.pad.labs.client.NodeClient;
import md.utm.pad.labs.response.Response;
import md.utm.pad.labs.service.JsonService;

import java.util.Optional;

/**
 * Created by anrosca on Nov, 2017
 */
public class ClientHandler implements Runnable {
    private final ClientChannel channel;
    private final NodeClient nodeClient;
    private final JsonService jsonService;

    public ClientHandler(ClientChannel channel, NodeClient nodeClient, JsonService jsonService) {
        this.channel = channel;
        this.nodeClient = nodeClient;
        this.jsonService = jsonService;
    }

    @Override
    public void run() {
        while (true) {
            Optional<String> request = ChannelUtil.readJsonRequest(channel);
            if (!request.isPresent())
                break;
            if (!request.get().isEmpty()) {
                Response response = nodeClient.submit(request.get());
                channel.write(jsonService.toJson(response));
            }
        }
    }
}

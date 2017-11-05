package md.utm.pad.labs.mediator.client;

import md.utm.pad.labs.channel.ClientChannel;
import md.utm.pad.labs.channel.SocketClientChannel;
import md.utm.pad.labs.channel.util.ChannelUtil;
import md.utm.pad.labs.config.MediatorConfiguration;
import md.utm.pad.labs.domain.Student;
import md.utm.pad.labs.response.Response;
import md.utm.pad.labs.service.JsonService;
import md.utm.pad.labs.service.impl.JacksonJsonService;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by anrosca on Nov, 2017
 */
public class Client implements AutoCloseable {
    private final MediatorConfiguration configuration;
    private final JsonService jsonService;
    private ClientChannel clientChannel;

    public Client(MediatorConfiguration configuration, JsonService jsonService) {
        this.configuration = configuration;
        this.jsonService = jsonService;
        setUpMediatorConnection();
    }

    private void setUpMediatorConnection() {
        try {
            Socket socket = new Socket(InetAddress.getByName(configuration.getMediatorAddress()), configuration.getMediatorPort());
            clientChannel = new SocketClientChannel(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        clientChannel.close();
    }

    public List<Student> send(String dsl) {
        clientChannel.write(dsl);
        Optional<String> jsonResponse = ChannelUtil.readJsonRequest(clientChannel);
        if (jsonResponse.isPresent()) {
            Response response = jsonService.fromJson(jsonResponse.get(), Response.class);
            return response.getResponseData();
        }
        return Collections.emptyList();
    }
}

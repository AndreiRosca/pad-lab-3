package md.utm.pad.labs.node.tcp;

import md.utm.pad.labs.channel.SocketClientChannel;

public class NodeConsumerHandler implements Runnable {
    private final SocketClientChannel clientChannel;

    public NodeConsumerHandler(SocketClientChannel clientChannel) {
        this.clientChannel = clientChannel;
    }

    public void run() {

    }

    private String readJsonRequest() {
        StringBuilder requestBuilder = new StringBuilder();
        String line;
        while ((line = clientChannel.readLine()) != null && line.trim().length() > 0) {
            requestBuilder.append(line);
        }
        return requestBuilder.toString();
    }
}

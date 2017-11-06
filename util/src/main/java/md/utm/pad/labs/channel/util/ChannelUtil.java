package md.utm.pad.labs.channel.util;

import md.utm.pad.labs.channel.ClientChannel;

import java.util.Optional;

/**
 * Created by anrosca on Nov, 2017
 */
public class ChannelUtil {

    public static Optional<String> readRequest(ClientChannel channel) {
        StringBuilder requestBuilder = new StringBuilder();
        String line;
        while ((line = channel.readLine()) != null && line.trim().length() > 0) {
            requestBuilder.append(line);
        }
        return line == null ? Optional.empty() : Optional.of(requestBuilder.toString());
    }
}

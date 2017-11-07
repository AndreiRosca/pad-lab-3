package md.utm.pad.labs.channel;

import javafx.util.Pair;
import md.utm.pad.labs.channel.util.ChannelUtil;
import md.utm.pad.labs.service.JsonService;
import md.utm.pad.labs.service.XmlService;
import md.utm.pad.labs.service.impl.JacksonJsonService;
import md.utm.pad.labs.service.impl.JaxbXmlService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by anrosca on Nov, 2017
 */
public class ResponseUtil {
    private static final Pattern MEDIA_TYPE_PATTERN = Pattern.compile("Content-Type: (?<mediaType>.+)");
    private static final JsonService jsonService = new JacksonJsonService();
    private static final XmlService xmlService = new JaxbXmlService();

    public static Pair<String, String> readResponse(ClientChannel channel) {
        String response = ChannelUtil.readRequest(channel).get();
        String header = response.substring(0, response.indexOf("\n"));
        response = response.substring(response.indexOf("\n"), response.length());
        return new Pair<>(header, response);
    }

    public static boolean isResponseXml(String response) {
        Matcher matcher = MEDIA_TYPE_PATTERN.matcher(response);
        if (matcher.find()) {
            String mediaType = matcher.group("mediaType");
            return xmlService.getMediaType().equalsIgnoreCase(mediaType);
        }
        return false;
    }

    public static boolean isResponseJson(String response) {
        Matcher matcher = MEDIA_TYPE_PATTERN.matcher(response);
        if (matcher.find()) {
            String mediaType = matcher.group("mediaType");
            return jsonService.getMediaType().equalsIgnoreCase(mediaType);
        }
        return false;
    }
}

package md.utm.pad.labs.response;

public class Response {
    private String type;
    private String payload;

    public Response() {
    }

    public Response(ResponseType responseType, String payload) {
        this.type = responseType.toString();
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Response{" +
                "type='" + type + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }
}

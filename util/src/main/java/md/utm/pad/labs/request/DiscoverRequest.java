package md.utm.pad.labs.request;

public class DiscoverRequest {
    private String type;

    public DiscoverRequest(DiscoverRequestType requestType) {
        this.type = requestType.toString();
    }

    protected DiscoverRequest() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "DiscoverRequest{" +
                "type='" + type + '\'' +
                '}';
    }
}

package md.utm.pad.labs.request;

public class Request {
    private String type;

    public Request(RequestType requestType) {
        this.type = requestType.toString();
    }

    protected Request() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Request{" +
                "type='" + type + '\'' +
                '}';
    }
}

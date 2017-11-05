package md.utm.pad.labs.request;

/**
 * Created by anrosca on Nov, 2017
 */
public class Request {
    private String request;

    public Request() {
    }

    public Request(String request) {
        this.request = request;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    @Override
    public String toString() {
        return "Request{" +
                "request='" + request + '\'' +
                '}';
    }
}

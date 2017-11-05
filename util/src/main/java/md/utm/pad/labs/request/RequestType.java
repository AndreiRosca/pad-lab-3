package md.utm.pad.labs.request;

/**
 * Created by anrosca on Nov, 2017
 */
public enum RequestType {
    GET_ALL("getAll");

    private final String requestType;

    RequestType(String requestType) {
        this.requestType = requestType;
    }

    public String toString() {
        return requestType;
    }

    public String getRequestType() {
        return requestType;
    }
}

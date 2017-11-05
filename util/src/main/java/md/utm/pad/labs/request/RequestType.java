package md.utm.pad.labs.request;

public enum RequestType {

    PRESENT("presentRequest");

    private String requestType;

    RequestType(String requestType) {
        this.requestType = requestType;
    }

    public String toString() {
        return requestType;
    }
}

package md.utm.pad.labs.request;

public enum DiscoverRequestType {

    PRESENT("presentRequest");

    private String requestType;

    DiscoverRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String toString() {
        return requestType;
    }
}

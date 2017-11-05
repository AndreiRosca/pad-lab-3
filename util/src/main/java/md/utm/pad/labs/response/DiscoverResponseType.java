package md.utm.pad.labs.response;

public enum DiscoverResponseType {
    PRESENT_RESPONSE("presentResponse");

    private String responseType;

    DiscoverResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String toString() {
        return responseType;
    }
}

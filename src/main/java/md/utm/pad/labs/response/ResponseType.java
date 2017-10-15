package md.utm.pad.labs.response;

public enum ResponseType {
    PRESENT_RESPONSE("presentResponse");

    private String responseType;

    ResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String toString() {
        return responseType;
    }
}

package md.utm.pad.labs.response;

/**
 * Created by anrosca on Nov, 2017
 */
public enum ResponseType {

    GET_ALL("getAll");

    private final String responseType;

    ResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getResponseType() {
        return responseType;
    }

    @Override
    public String toString() {
        return responseType;
    }
}

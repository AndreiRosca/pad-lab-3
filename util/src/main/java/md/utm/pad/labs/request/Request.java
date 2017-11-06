package md.utm.pad.labs.request;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by anrosca on Nov, 2017
 */
@XmlRootElement
public class Request {
    private String dslRequest;

    public Request() {
    }

    public Request(String dslRequest) {
        this.dslRequest = dslRequest;
    }

    public String getDslRequest() {
        return dslRequest;
    }

    public void setDslRequest(String dslRequest) {
        this.dslRequest = dslRequest;
    }

    @Override
    public String toString() {
        return "Request{" +
                "dslRequest='" + dslRequest + '\'' +
                '}';
    }
}

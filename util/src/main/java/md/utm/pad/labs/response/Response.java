package md.utm.pad.labs.response;

import md.utm.pad.labs.domain.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anrosca on Nov, 2017
 */
public class Response {
    private String type;
    private List<Student> responseData = new ArrayList<>();

    public Response() {
    }

    public Response(ResponseType responseType) {
        this.type = responseType.toString();
    }

    public Response(String type) {
        this.type = type;
    }

    public Response(ResponseType responseType, List<Student> students) {
        this(responseType);
        this.responseData = students;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Student> getResponseData() {
        return responseData;
    }

    public void setResponseData(List<Student> responseData) {
        this.responseData = responseData;
    }

    @Override
    public String toString() {
        return "Response{" +
                "type='" + type + '\'' +
                ", responseData=" + responseData +
                '}';
    }
}

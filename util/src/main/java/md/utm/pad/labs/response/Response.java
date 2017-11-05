package md.utm.pad.labs.response;

import md.utm.pad.labs.domain.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anrosca on Nov, 2017
 */
public class Response {
    private List<Student> responseData = new ArrayList<>();

    public Response() {
    }

    public Response(List<Student> students) {
        this.responseData = students;
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
                "responseData=" + responseData +
                '}';
    }
}

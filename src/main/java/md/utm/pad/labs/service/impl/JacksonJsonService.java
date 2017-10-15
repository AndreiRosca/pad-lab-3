package md.utm.pad.labs.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import md.utm.pad.labs.service.JsonService;

import java.io.IOException;

public class JacksonJsonService implements JsonService {
    @Override
    public <T> String toJson(T object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T fromJson(String json, Class<T> resultingClass) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, resultingClass);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

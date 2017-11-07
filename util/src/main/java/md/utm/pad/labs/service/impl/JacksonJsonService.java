package md.utm.pad.labs.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import md.utm.pad.labs.request.Request;
import md.utm.pad.labs.service.JsonService;
import md.utm.pad.labs.service.RequestSerializer;

public class JacksonJsonService implements JsonService, RequestSerializer {
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

    @Override
    public <T> String serialize(T object, Class<T> targetClass) {
        return toJson(object);
    }

    @Override
    public <T> T deserialize(String data, Class<T> resultingClass) {
        return fromJson(data, resultingClass);
    }

    @Override
    public String getMediaType() {
        return "application/json";
    }
}

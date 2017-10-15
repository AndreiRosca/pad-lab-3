package md.utm.pad.labs.service;

public interface JsonService {

    <T> String toJson(T object);
    <T> T fromJson(String json, Class<T> resultingClass);
}

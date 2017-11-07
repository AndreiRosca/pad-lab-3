package md.utm.pad.labs.service;

public interface RequestSerializer {

    <T> String serialize(T object, Class<T> targetClass);
    <T> T deserialize(String data, Class<T> resultingClass);
    String getMediaType();
}

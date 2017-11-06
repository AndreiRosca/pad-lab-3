package md.utm.pad.labs.service;

public interface XmlService {

    <T> String toXml(T object, Class<T> targetClass);
    <T> T fromXml(String xml, Class<T> resultingClass);
}

package md.utm.pad.labs.service;

public interface XmlService extends RequestSerializer {

    <T> String toXml(T object, Class<T> targetClass);
    <T> T fromXml(String xml, Class<T> resultingClass);
}

package md.utm.pad.labs.service.impl;

import md.utm.pad.labs.service.RequestSerializer;
import md.utm.pad.labs.service.XmlService;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Created by anrosca on Nov, 2017
 */
public class JaxbXmlService implements XmlService, RequestSerializer {
    private static final Logger LOGGER = Logger.getLogger(JaxbXmlService.class);

    @Override
    public <T> String toXml(T object, Class<T> targetClass) {
        try {
            return tryConvertToXml(object, targetClass);
        } catch (JAXBException e) {
            LOGGER.error("Can't marshal to xml", e);
            throw new RuntimeException(e);
        }
    }

    private <T> String tryConvertToXml(T object, Class<T> targetClass) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(targetClass);
        Marshaller marshaller = context.createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(object, writer);
        return writer.toString();
    }

    @Override
    public <T> T fromXml(String xml, Class<T> resultingClass) {
        try {
            return tryConvertFromXml(xml, resultingClass);
        } catch (JAXBException e) {
            LOGGER.error("Can't parse the xml", e);
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T tryConvertFromXml(String xml, Class<T> resultingClass) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(resultingClass);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (T) unmarshaller.unmarshal(new StringReader(xml));
    }

    @Override
    public <T> String serialize(T object, Class<T> targetClass) {
        return toXml(object, targetClass);
    }

    @Override
    public <T> T deserialize(String data, Class<T> resultingClass) {
        return deserialize(data, resultingClass);
    }

    @Override
    public String getMediaType() {
        return "application/xml";
    }
}

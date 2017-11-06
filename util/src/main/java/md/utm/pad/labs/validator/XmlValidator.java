package md.utm.pad.labs.validator;

import org.apache.log4j.Logger;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.StringReader;

/**
 * Created by anrosca on Nov, 2017
 */
public class XmlValidator implements ValidationEventHandler {
    private static final Logger LOGGER = Logger.getLogger(XmlValidator.class);

    private Class<?> targetClass;
    private String schemaFile;
    private boolean isValid = true;

    public XmlValidator(Class<?> targetClass, String schemaFile) {
        this.targetClass = targetClass;
        this.schemaFile = schemaFile;
    }

    public boolean validate(String xml) {
        try {
            JAXBContext context = JAXBContext.newInstance(targetClass);
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(getSchemaFile());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setSchema(schema);
            unmarshaller.setEventHandler(this);
            unmarshaller.unmarshal(new StringReader(xml));
            return isValid;
         } catch (Exception e) {
            LOGGER.error("Error while validating the xml file.", e);
        }
        return false;
    }

    private File getSchemaFile() {
        ClassLoader loader = getClass().getClassLoader();
        return new File(loader.getResource(schemaFile).getFile());
    }

    @Override
    public boolean handleEvent(ValidationEvent event) {
        LOGGER.info("SEVERITY:  " + event.getSeverity());
        LOGGER.info("MESSAGE:  " + event.getMessage());
        LOGGER.info("LINKED EXCEPTION:  " + event.getLinkedException());
        LOGGER.info("LOCATOR");
        LOGGER.info("LINE NUMBER:  " + event.getLocator().getLineNumber());
        LOGGER.info("COLUMN NUMBER:  " + event.getLocator().getColumnNumber());
        LOGGER.info("OFFSET:  " + event.getLocator().getOffset());
        LOGGER.info("OBJECT:  " + event.getLocator().getObject());
        LOGGER.info("NODE:  " + event.getLocator().getNode());
        LOGGER.info("URL:  " + event.getLocator().getURL());
        return event.getSeverity() != ValidationEvent.ERROR &&
                event.getSeverity() != ValidationEvent.FATAL_ERROR;
    }
}

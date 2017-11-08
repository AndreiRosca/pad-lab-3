package md.utm.pad.labs.validator;

/**
 * Created by anrosca on Nov, 2017
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import java.io.IOException;
import java.io.InputStreamReader;


public class JsonValidator {

    private static JsonNode loadResource(final String name) throws IOException {
        return JsonLoader.fromReader(new InputStreamReader(JsonValidator.class.getResourceAsStream(name)));
    }

    public static boolean validate(String json, String schemaFile) {
        try {
            return tryValidate(json, schemaFile);
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
    }

    private static boolean tryValidate(String json, String schemaFile) throws IOException, ProcessingException {
        JsonNode schemaNode = loadResource(schemaFile);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        JsonSchema schema = factory.getJsonSchema(schemaNode);
        ProcessingReport report = schema.validate(jsonNode);
        return report.isSuccess();
    }
}

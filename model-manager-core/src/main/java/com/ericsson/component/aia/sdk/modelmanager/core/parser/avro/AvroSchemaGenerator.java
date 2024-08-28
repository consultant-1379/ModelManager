package com.ericsson.component.aia.sdk.modelmanager.core.parser.avro;

import static com.ericsson.component.aia.sdk.modelmanager.core.parser.avro.AvroGeneratorUtils.generateAvroSchemasForSchemaTypeAndFfvFiv;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.avro.AvroGeneratorUtils.generateBaseAvroSchemasForSchemaType;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.avro.AvroGeneratorUtils.loadEventParamMap;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.ResourceNotFoundException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.Event;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.Schema;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.type.FileBasedSchemaHandler;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.type.SchemaEnum;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.FfvFivKey;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.MappedEvent;

/**
 * This class generates JSON file for Avro schema generation
 */
public class AvroSchemaGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvroSchemaGenerator.class);

    /**
     * Entry method for generating avro schemas from the command line.
     *
     * @param args
     *            input and output directories
     * @throws SchemaException
     *             if schema is malformed
     * @throws IOException
     *             if input directory doesn't exist
     */
    public static void main(final String args[]) throws SchemaException, IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Main method takes two arguments only, " + "args[0] => the input directory the xmls are stored, "
                    + "args[1] => the output directory the outputted avro schemas are stored");
        }
        final String inputDirectory = args[0];
        final String outputDirectory = args[1];
        LOGGER.info("Converting xmls in directory {} to avro schemas and outputting new avros schemas to {}", inputDirectory, outputDirectory);
        final AvroSchemaGenerator jsonGenerator = new AvroSchemaGenerator();
        jsonGenerator.generate(inputDirectory, outputDirectory);
    }

    /**
     * Generates avro schemas.
     *
     * @param inputDirectory
     *            to read schema xmls from.
     * @param outputDirectory
     *            to output avro schemas to.
     * @throws SchemaException
     *             if schema is malformed
     * @throws IOException
     *             if input directory doesn't exist
     */
    public void generate(final String inputDirectory, final String outputDirectory) throws SchemaException, IOException {
        addDirectoryToClasspath(inputDirectory);
        generateAvroSchemas(outputDirectory);
    }

    private void addDirectoryToClasspath(final String metadataDirectoryName) {
        GenerateUtils.addDirectoryToClasspath(new File(metadataDirectoryName));
    }

    private void generateAvroSchemas(final String outputDirectory) throws SchemaException, IOException {
        for (final SchemaEnum schemaType : SchemaEnum.values()) {
            final JsonGenSchemaHandler schemaHandler = new JsonGenSchemaHandler(schemaType).load();
            if (schemaHandler.getSchemaTypeHandler() != null) {
                generateBaseAvroSchemasForSchemaType(outputDirectory, schemaType, schemaHandler.getEventMap());
                generateAvroSchemasForSchemaTypeAndFfvFiv(outputDirectory, schemaType, schemaHandler.getSchemaMap());
            } else {
                LOGGER.info("No schemas available for schema type {}" + schemaType.name());
            }
        }
    }

    /**
     * @param schemaType
     *            - schema
     * @param xml
     *            - xml
     * @param eventName
     *            - event
     * @return avro schema
     * @throws SchemaException
     *             - exception
     * @throws IOException
     *             - exception
     */
    String generateAvroSchema(final SchemaEnum schemaType, final String xml, final String eventName) throws SchemaException, IOException {
        final JsonGenSchemaHandler schemaHandler = new JsonGenSchemaHandler(schemaType).load();
        if (schemaHandler.getSchemaTypeHandler() != null) {
            return AvroGeneratorUtils.generateBaseAvroSchemaForSchemaType(schemaType, schemaHandler.getEventMap(), eventName);
        }
        return null;
    }

    /**
     * @author aia
     */
    class JsonGenSchemaHandler extends FileBasedSchemaHandler {

        private final Map<Event, MappedEvent> eventMap = new TreeMap<>();

        /**
         * @param neType
         *            - ne type
         */
        JsonGenSchemaHandler(final SchemaEnum neType) {
            super(neType);
        }

        @Override
        public JsonGenSchemaHandler load() throws ResourceNotFoundException, SchemaException {
            super.load();
            buildEventParameterMap();
            return this;
        }

        /**
         * Build the event parameter map using the rules described in the comment at the top of this class
         */
        private void buildEventParameterMap() {
            for (final Schema schema : schemaMap.descendingMap().values()) {
                loadEventParamMap(eventMap, schema);
            }
        }

        /**
         * @return event
         */
        public Map<Event, MappedEvent> getEventMap() {
            return eventMap;
        }

        /**
         * @return event
         */
        public Map<FfvFivKey, Schema> getSchemaMap() {
            return schemaMap;
        }

    }

}
/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.sdk.modelmanager.core.parser.avro;

import static com.ericsson.component.aia.sdk.modelmanager.core.parser.avro.AvroGeneratorUtils.generateAvroSchemasForSchemaTypeAndFfvFiv;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.avro.AvroGeneratorUtils.generateBaseAvroSchemasForSchemaType;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.avro.AvroGeneratorUtils.loadEventParamMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.jaxen.JaxenException;
import org.jdom.JDOMException;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.ResourceNotFoundException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.handler.SchemaTypeHandler;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.handler.SchemaTypeLoader;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.Event;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.Schema;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.SchemaBuilder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.type.FileBasedSchemaHandler;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.type.SchemaEnum;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.type.SchemaProvider;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.FfvFivKey;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.MappedEvent;

/**
 * An class for transforming node xmls in a specified directory into avro schemas.
 */
public class GenericAvroSchemaGenerator {

    private final Map<Event, MappedEvent> events;
    private final Map<FfvFivKey, Schema> schemas;
    private final SchemaEnum schemaType;
    private final XmlFileVisitor visitor = new XmlFileVisitor();

    /**
     * Constructs an AvroSchemaGenerator for specified {@code schemaType}.
     *
     * @param schemaType
     *            the type of schemas to be processed.
     */
    public GenericAvroSchemaGenerator(final SchemaEnum schemaType) {
        this.schemaType = schemaType;
        events = new TreeMap<>();
        schemas = new HashMap<>();
    }

    /**
     * Parses any xml files in the specified {@code inputFilePath} and generates avro schemas in the specified {@code outputDirectory} from them.
     *
     * @param inputFilePath
     *            a directory containing xml files or an xml file.
     * @param outputDirectory
     *            a directory where the avro schema output will be stored.
     * @throws IOException
     *             if the input directory or file does not exist.
     * @throws JaxenException
     *             if the xml can't be parsed.
     * @throws SchemaException
     *             if the xml can't be parsed.
     * @throws JDOMException
     *             if the xml can't be parsed.
     */
    public void generate(final String inputFilePath, final String outputDirectory)
            throws IOException, JaxenException, SchemaException, JDOMException {
        GenerateUtils.addDirectoryToClasspath(new File(inputFilePath));
        Files.walkFileTree(Paths.get(inputFilePath), visitor);
        new AvroSchemaHandler(schemaType).load();
        generateAvroFromXmlFile(outputDirectory);
    }

    /**
     * Parses an xml file that is specified {@code inputFilePath} and generates avro schemas in the specified {@code outputDirectory} from it.
     *
     * @param inputFilePath
     *            Absolute file path of xml.
     * @param outputDirectory
     *            a directory where the avro schema output will be stored.
     * @throws IOException
     *             if the input directory or file does not exist.
     * @throws JaxenException
     *             if the xml can't be parsed.
     * @throws SchemaException
     *             if the xml can't be parsed.
     * @throws JDOMException
     *             if the xml can't be parsed.
     */
    public void generateAvroForSingleFile(final String inputFilePath, final String outputDirectory)
            throws IOException, JaxenException, SchemaException, JDOMException {
        new AvroSchemaHandler(schemaType).loadFile(inputFilePath);
        generateAvroFromXmlFile(outputDirectory);
    }

    /**
     * @param xml
     *            - xml
     * @param eventName
     *            - event name
     * @return avro schema
     * @throws IOException
     *             - exception
     * @throws JaxenException
     *             - exception
     * @throws SchemaException
     *             - exception
     * @throws JDOMException
     *             - exception
     */
    public String generateAvroForSingleXML(final String xml, final String eventName)
            throws IOException, JaxenException, SchemaException, JDOMException {
        new AvroSchemaHandler(schemaType).loadFile(xml);

        return AvroGeneratorUtils.generateBaseAvroSchemaForSchemaType(schemaType, events, eventName);
    }

    private void generateAvroFromXmlFile(final String outputDirectory) throws SchemaException, JDOMException, IOException, JaxenException {
        generateBaseAvroSchemasForSchemaType(outputDirectory, schemaType, events);
        generateAvroSchemasForSchemaTypeAndFfvFiv(outputDirectory, schemaType, schemas);
    }

    Map<Event, MappedEvent> getEvents() {
        return new HashMap<>(events);
    }

    Map<FfvFivKey, Schema> getSchemas() {
        return new HashMap<>(schemas);
    }

    /**
     * SchemaHandler that loads schemas for all xml files in a given directory instead of all xml files in /xml directory on the classpath like
     * {@link AvroSchemaGenerator}
     */
    class AvroSchemaHandler extends FileBasedSchemaHandler {
        private final SchemaEnum neType;

        private AvroSchemaHandler(final SchemaEnum neType) {
            super(neType);
            this.neType = neType;
        }

        @Override
        public SchemaProvider load() throws SchemaException, ResourceNotFoundException {
            final SchemaTypeHandler handler = new SchemaTypeLoader().getSchemaTypeMap().get(neType);
            for (final Path xmlFilePath : visitor.getXmlFilePaths()) {
                loadXmlFile(xmlFilePath.toString(), handler);
            }
            return this;
        }

        /**
         * Load file for avro conversion.
         *
         * @param filePath
         *            the absolute file path to load.
         * @return load Schema Type Xml and Schema xml files
         * @throws ResourceNotFoundException
         *             - exception
         * @throws SchemaException
         *             - exception
         */
        public SchemaProvider loadFile(final String filePath) throws SchemaException, ResourceNotFoundException {
            final SchemaTypeHandler handler = new SchemaTypeLoader().getSchemaTypeMap().get(neType);
            loadXmlFile(filePath, handler);
            return this;
        }

        private void loadXmlFile(final String filePath, final SchemaTypeHandler handler) throws SchemaException {
            try {
                final Schema schema = new SchemaBuilder(handler.getName(), filePath).paramPreamble(handler.getParamPreamble())
                        .valuePreamble(handler.getValuePreamble()).build();
                schemas.put(schema.getGeneralHandler().getGeneralInfo().getFfvFivKey(), schema);
                loadEventParamMap(events, schema);
            } catch (JaxenException | JDOMException | IOException exception) {
                LOGGER.error("loading of schema of type {} from file {} failed ", handler.getName(), filePath, exception);
            }
        }

    }
}

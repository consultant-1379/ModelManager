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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.Event;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.EventParameter;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.Schema;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.type.SchemaEnum;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.FfvFivKey;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.MappedEvent;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParserType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Contains helper methods for generating avro schemas.
 */
public final class AvroGeneratorUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvroGeneratorUtils.class);
    private static final String AVRO_FILE_EXTENSION = ".avsc";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String DEFAULT = "default";

    private AvroGeneratorUtils() {

    }

    /**
     * @param directoryName
     *            - folder name
     * @param eventName
     *            - event name
     * @return file name
     */
    static String getFileName(final String directoryName, final String eventName) {
        final StringBuilder fileName = new StringBuilder();
        fileName.append(directoryName);
        fileName.append(File.separator);
        fileName.append(eventName);
        fileName.append(AVRO_FILE_EXTENSION);
        return fileName.toString();
    }

    /**
     * @param eventMap
     *            - event map
     * @param schema
     *            - schema
     */
    static void loadEventParamMap(final Map<Event, MappedEvent> eventMap, final Schema schema) {
        for (final Event event : schema.getEventHandler().getMap().values()) {
            if (!eventMap.containsKey(event)) {
                eventMap.put(event, new MappedEvent(event));
            }
            final MappedEvent mappedEvent = eventMap.get(event);
            mappedEvent.setParameters(event);
        }
    }

    /**
     * @param fields
     *            - fields
     * @param eventId
     *            - event id
     */
    static void updateDefaultFields(final JsonArray fields, final int eventId) {
        final JsonObject neField = new JsonObject();
        neField.addProperty(NAME, "_NE");
        neField.addProperty(TYPE, "string");
        fields.add(neField);

        final JsonObject timeStampField = new JsonObject();
        timeStampField.addProperty(NAME, "_TIMESTAMP");
        timeStampField.addProperty(TYPE, "long");
        fields.add(timeStampField);

        final JsonObject eventIdField = new JsonObject();
        eventIdField.addProperty(NAME, "_ID");
        eventIdField.addProperty(TYPE, "int");
        eventIdField.addProperty(DEFAULT, eventId);
        fields.add(eventIdField);

    }

    /**
     * @param directoryName
     *            - folder name
     * @param event
     *            - event
     * @return json object
     */
    static JsonObject createHeader(final String directoryName, final Event event) {
        final JsonObject schemaJson = new JsonObject();
        schemaJson.addProperty(TYPE, "record");
        schemaJson.addProperty(NAME, event.getName());
        schemaJson.addProperty("namespace", getNameSpace(directoryName));
        return schemaJson;
    }

    /**
     * @param directoryName
     *            - folder name
     * @return namespace
     */
    static String getNameSpace(final String directoryName) {
        return directoryName.substring(directoryName.lastIndexOf(File.separator) + 1, directoryName.length());
    }

    /**
     * @param eventParameters
     *            - event
     * @param eventId
     *            - event id
     * @return json array
     */
    static JsonArray createFields(final Collection<EventParameter> eventParameters, final int eventId) {
        final JsonArray fields = new JsonArray();
        updateDefaultFields(fields, eventId);
        for (final EventParameter element : eventParameters) {
            fields.add(createField(element));
        }
        return fields;
    }

    /**
     * @param element
     *            - element
     * @return json
     */
    private static JsonObject createField(final EventParameter element) {
        final JsonObject field = new JsonObject();
        field.addProperty(NAME, element.getName());
        if ((element.isEventArrayParameter() || element.isStructArray()) && element.getParserType() == ParserType.BYTE) {
            field.addProperty(TYPE, TypeConversionParserTypesToJson.typeSqlToJava(ParserType.BYTE_ARRAY));
            return field;
        } else if (element.isStructArray() || element.isEventArrayParameter()) {
            final JsonObject fieldType = new JsonObject();
            fieldType.addProperty(TYPE, "array");
            fieldType.addProperty("items", TypeConversionParserTypesToJson.typeSqlToJava(element.getParserType()));
            field.add(TYPE, fieldType);
            return field;
        }
        field.addProperty(TYPE, TypeConversionParserTypesToJson.typeSqlToJava(element.getParserType()));
        return field;
    }

    /**
     * @param outputDirectory
     *            - output folder
     * @param schemaType
     *            - schema type
     * @param events
     *            - events
     * @throws IOException
     *             - exception
     */
    public static void generateBaseAvroSchemasForSchemaType(final String outputDirectory, final SchemaEnum schemaType,
                                                            final Map<Event, MappedEvent> events)
            throws IOException {
        LOGGER.info("Conversion process for base Avro schema for schema type {} starting.", schemaType.value());
        if (events != null && !events.isEmpty()) {
            generateBaseAvroSchemas(outputDirectory, schemaType, events.values());
        }
        LOGGER.info("Conversion process for base Avro schema for schema type {} completed.", schemaType.value());
    }

    /**
     * @param schemaType
     *            - schema type
     * @param events
     *            - events
     * @param eventName
     *            - event name
     * @return schema
     * @throws IOException
     *             - exception
     */
    public static String generateBaseAvroSchemaForSchemaType(final SchemaEnum schemaType, final Map<Event, MappedEvent> events,
                                                             final String eventName)
            throws IOException {
        LOGGER.info("Conversion process for base Avro schema for schema type {}.", schemaType.value());
        if (events != null && !events.isEmpty()) {
            final MappedEvent mappedEvent = events.values().stream()
                    .filter(evt -> StringUtils.isEmpty(eventName) || evt.getEvent().getName().equalsIgnoreCase(eventName)).findFirst().get();
            return createAvroSchemaJson(schemaType.name(), mappedEvent.getEvent());
        }
        return null;
    }

    /**
     * @param outputDirectory
     *            - output folder
     * @param schemaType
     *            - schema type
     * @param mappedEvents
     *            - events
     * @throws IOException
     *             - exception
     */
    public static void generateBaseAvroSchemas(final String outputDirectory, final SchemaEnum schemaType, final Collection<MappedEvent> mappedEvents)
            throws IOException {
        final String featureDir = getDirectoryName(outputDirectory, schemaType.value());
        createDirectory(featureDir);
        for (final MappedEvent mappedEvent : mappedEvents) {
            createBaseAvroSchemaJsonFile(featureDir, mappedEvent);
        }
    }

    /**
     * @param outputDirectory
     *            - output folder
     * @param schemaType
     *            - schema type
     * @param ffvFivKeyToschemaMap
     *            - map
     * @throws IOException
     *             - exception
     */
    public static void generateAvroSchemasForSchemaTypeAndFfvFiv(final String outputDirectory, final SchemaEnum schemaType,
                                                                 final Map<FfvFivKey, Schema> ffvFivKeyToschemaMap)
            throws IOException {
        LOGGER.info("Conversion process for {} starting.", schemaType.value());
        if (ffvFivKeyToschemaMap != null) {
            generateAvroSchemasForFFvFiv(outputDirectory, ffvFivKeyToschemaMap);
        }
        LOGGER.info("Conversion process for {} completed.", schemaType.value());
    }

    /**
     * @param outputDirectory
     *            - output folder
     * @param ffvFivKeyToschemaMap
     *            - map
     * @throws IOException
     *             - exception
     */
    public static void generateAvroSchemasForFFvFiv(final String outputDirectory, final Map<FfvFivKey, Schema> ffvFivKeyToschemaMap)
            throws IOException {
        for (final Entry<FfvFivKey, Schema> entry : ffvFivKeyToschemaMap.entrySet()) {
            final String dirName = getDirectoryName(outputDirectory, entry.getValue().getAvroPackageName());
            createDirectory(dirName);
            createJsonFiles(dirName, entry.getValue());
        }
    }

    /**
     * @param userDefinedOutputDirectory
     *            - folder
     * @param avroDirectoryName
     *            - avro folder
     * @return folder name
     */
    public static String getDirectoryName(final String userDefinedOutputDirectory, final String avroDirectoryName) {

        String dirName = "src" + File.separator + "main" + File.separator + "resources" + File.separator + avroDirectoryName;

        if (userDefinedOutputDirectory != null && !userDefinedOutputDirectory.isEmpty()) {
            dirName = userDefinedOutputDirectory + File.separator + avroDirectoryName;
        }

        return dirName;
    }

    /**
     * @param directoryName
     *            - folder name
     * @throws IOException
     *             - exception
     */
    public static void createDirectory(final String directoryName) throws IOException {

        final File dir = new File(directoryName);
        FileUtils.deleteDirectory(dir);
        if (!dir.mkdirs()) {
            throw new IOException("Unable to create " + directoryName);
        }
        LOGGER.info("Output Directory created: {}", directoryName);
    }

    /**
     * @param directoryName
     *            - folder name
     * @param schema
     *            - schema
     * @throws FileNotFoundException
     *             - exception
     */
    public static void createJsonFiles(final String directoryName, final Schema schema) throws FileNotFoundException {
        for (final Event event : schema.getEventHandler().getMap().values()) {
            createAvroSchemaJsonFile(directoryName, event);
        }
    }

    /**
     * @param namespace
     *            - namespace
     * @param event
     *            - event
     * @return avro schema
     * @throws FileNotFoundException
     *             - exception
     */
    public static String createAvroSchemaJson(final String namespace, final Event event) throws FileNotFoundException {
        final JsonObject schemaJson = new JsonObject();
        schemaJson.addProperty(TYPE, "record");
        schemaJson.addProperty(NAME, event.getName());
        schemaJson.addProperty("namespace", namespace);
        final JsonArray fields = createFields(event.getParameterList(), event.getId());
        schemaJson.add("fields", fields);
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(schemaJson);
    }

    /**
     * @param directoryName
     *            - folder name
     * @param event
     *            - event
     * @throws FileNotFoundException
     *             - exception
     */
    public static void createAvroSchemaJsonFile(final String directoryName, final Event event) throws FileNotFoundException {
        final JsonObject schemaJson = createHeader(directoryName, event);
        final JsonArray fields = createFields(event.getParameterList(), event.getId());
        schemaJson.add("fields", fields);
        final String fileName = getFileName(directoryName, event.getName());
        createAvroSchemaJsonFile(schemaJson, fileName);
    }

    /**
     * @param directoryName
     *            - folder
     * @param mappedEvent
     *            - event
     * @throws FileNotFoundException
     *             - exception
     */
    public static void createBaseAvroSchemaJsonFile(final String directoryName, final MappedEvent mappedEvent) throws FileNotFoundException {
        final JsonObject schemaJson = createHeader(directoryName, mappedEvent.getEvent());
        final JsonArray fields = createFields(mappedEvent.getParameterSet(), mappedEvent.getEvent().getId());
        schemaJson.add("fields", fields);
        final String fileName = getFileName(directoryName, mappedEvent.getEvent().getName());
        createAvroSchemaJsonFile(schemaJson, fileName);
    }

    /**
     * @param jsonObject
     *            - object
     * @param fileName
     *            - file
     * @throws FileNotFoundException
     *             - exception
     */
    public static void createAvroSchemaJsonFile(final JsonObject jsonObject, final String fileName) throws FileNotFoundException {
        final File file = new File(fileName);
        final PrintWriter out = new PrintWriter(file);

        LOGGER.debug("Creating avro schema file for: {}", fileName);
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final String prettyJsonString = gson.toJson(jsonObject);
        out.println(prettyJsonString);
        out.flush();
        out.close();
    }
}

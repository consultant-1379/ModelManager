/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.sdk.modelmanager.core.parser.model;

import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.SchemaUtil.EVENT_DEFINITION_NAMESPACE;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jaxen.JaxenException;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.ResourceNotFoundException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.handler.XMLDocumentHandler;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.resource.ResourceFileFinder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.resource.ResourceProvider;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.type.SchemaEnum;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.FfvFivKey;

/**
 * This class builds an event schema from an event definition XML file
 *
 */
public class Schema {

    private static final Logger LOGGER = LoggerFactory.getLogger(Schema.class);

    private static final String EVENT_NAMESPACE_PREFIX = "pe";

    private final Namespace namespace = Namespace.getNamespace(EVENT_DEFINITION_NAMESPACE);

    private final Map<String, String> nameSpaceMap = new HashMap<String, String>(); // Used by XPath searches

    private GeneralHandler generalHandler;

    private EventHandler eventHandler;

    private ParameterHandler parameterHandler;

    private StructureHandler structureHandler;

    private CategoryHandler categoryHandler;

    private final SchemaEnum name;

    private final String paramPreamble;

    private final String valuePreamble;

    private String packageName;

    private String avroPackageName;

    /**
     * Constructor.
     *
     * @param builder
     *            - builder
     * @throws JaxenException
     *             - JaxenException
     * @throws SchemaException
     *             - Exception
     * @throws JDOMException
     *             - Exception
     * @throws IOException
     *             - Exception
     */
    public Schema(final SchemaBuilder builder) throws JaxenException, SchemaException, JDOMException, IOException {
        this(builder.getName(), builder.getSchemaXMLFile(), builder.getSchemaXsdFile(), builder.getParamPreamble(), builder.getValuePreamble(),
                builder.getResourceProvider());
    }

    /**
     *
     * @param name
     *            name
     * @param schemaXMLFile
     *            file
     * @param schemaXsdFile
     *            file
     * @param paramPreamble
     *            - file
     * @param valuePreamble
     *            - value
     * @throws JaxenException
     *             - exception
     * @throws SchemaException
     *             - exception
     * @throws JDOMException
     *             - exception
     * @throws IOException
     *             - exception
     */
    public Schema(final SchemaEnum name, final String schemaXMLFile, final String schemaXsdFile, final String paramPreamble,
                  final String valuePreamble)
            throws JaxenException, SchemaException, JDOMException, IOException {
        this(name, schemaXMLFile, schemaXsdFile, paramPreamble, valuePreamble, new ResourceFileFinder());
    }

    /**
     * @param name
     *            neType
     * @param schemaXMLFile
     *            location of Schema xml file (i.e. 101/ schmea or Pm Schema)
     * @param schemaXsdFile
     *            location of Schema xsd (i.e. EventFormat.xsd)
     * @param paramPreamble
     *            Parameter preamble (text to trim in Event parameter's name i.e. <b>EVENT_PARAM_</b> will trim EVENT_PARAM_TIMESTAMP_HOUR parameter
     *            to TIMESTAMP_HOUR)
     * @param valuePreamble
     *            Value preamble (text to trim in enumeration value's name i.e <b>EVENT_VALUE</b> will trim EVENT_VALUE_SUCCESS to SUCCESS)
     * @param resourceProvider
     *            - resource
     * @throws SchemaException
     *             - Exception
     * @throws JDOMException
     *             - Exception
     * @throws IOException
     *             - Exception
     * @throws JaxenException
     *             - Exception
     */
    public Schema(final SchemaEnum name, final String schemaXMLFile, final String schemaXsdFile, final String paramPreamble,
                  final String valuePreamble, final ResourceProvider resourceProvider)
            throws SchemaException, JDOMException, IOException, JaxenException {
        this.name = name;
        this.paramPreamble = paramPreamble;
        this.valuePreamble = valuePreamble;

        logValues(schemaXMLFile, schemaXsdFile);
        checkSchemaXmlFile(schemaXMLFile);

        final XMLDocumentHandler documentHandler = getXMLDocumentHandler(resourceProvider, schemaXsdFile);
        final Document eventDocument = documentHandler.loadAndValidate(resourceProvider.getFileResourceAsStream(schemaXMLFile));

        loadSchema(name, eventDocument);
    }

    private void logValues(final String schemaXMLFile, final String schemaXsdFile) {
        LOGGER.debug("xmlFile={}", schemaXMLFile);
        LOGGER.debug("Namespace:{}", EVENT_DEFINITION_NAMESPACE);
        LOGGER.debug("Schema File:{}", schemaXsdFile);
        LOGGER.debug("XML File:{}", schemaXMLFile);
    }

    private void checkSchemaXmlFile(final String schemaXMLFile) throws SchemaException {
        if (schemaXMLFile == null) {
            throw new SchemaException("failed to find XML file for events: " + schemaXMLFile);
        }
    }

    private XMLDocumentHandler getXMLDocumentHandler(final ResourceProvider resourceFileFinder, final String schemaXsdFile)
            throws ResourceNotFoundException {
        final Map<String, String> eventSchemaMap = new HashMap<String, String>();
        eventSchemaMap.put(namespace.getURI(), resourceFileFinder.getFileResourcePath(schemaXsdFile));
        return new XMLDocumentHandler(eventSchemaMap);
    }

    private void loadSchema(final SchemaEnum name, final Document eventDocument) throws SchemaException, JaxenException, JDOMException, IOException {
        nameSpaceMap.put(EVENT_NAMESPACE_PREFIX, namespace.getURI());
        generalHandler = new GeneralHandler(this);
        generalHandler.buildMap(eventDocument);

        parameterHandler = new ParameterHandler(this, name.value());
        parameterHandler.buildMap(eventDocument);

        structureHandler = new StructureHandler(this);
        structureHandler.buildMap(eventDocument);

        eventHandler = new EventHandler(this);
        eventHandler.buildMap(eventDocument);

        categoryHandler = new CategoryHandler(this);
        categoryHandler.buildMap(eventDocument);

        this.packageName = getSchemaPackageName(name, generalHandler.getGeneralInfo().getFfvFivKey());
        this.avroPackageName = getAvroSchemaPackageName(name, generalHandler.getGeneralInfo().getFfvFivKey());
    }

    private String getSchemaPackageName(final SchemaEnum schemaType, final FfvFivKey ffvFivKey) {
        final StringBuilder builder = new StringBuilder(schemaType.value()).append("_");
        builder.append(ffvFivKey.getFileFormatVersion()).append("_").append(ffvFivKey.getFileInformationVersion());
        if (ffvFivKey.getIteration() != null) {
            builder.append("_").append(ffvFivKey.getIteration());
        }
        return builder.toString().toLowerCase();
    }

    private String getAvroSchemaPackageName(final SchemaEnum schemaType, final FfvFivKey ffvFivKey) {
        final StringBuilder builder = new StringBuilder(schemaType.value()).append(".");
        builder.append(ffvFivKey.getFileFormatVersion()).append(".").append(ffvFivKey.getFileInformationVersion());
        if (ffvFivKey.getIteration() != null) {
            builder.append(".v").append(ffvFivKey.getIteration());
        }
        return builder.toString().toLowerCase();
    }

    /**
     * Get the name of this schema
     *
     * @return the schema name
     */
    public SchemaEnum getName() {
        return name;
    }

    /**
     * Get the name space for this schema
     *
     * @return The name space for this schema
     */
    public Namespace getNameSpace() {
        return namespace;
    }

    /**
     * Get the name space map for this schema
     *
     * @return the name space map
     */
    public Map<String, String> getNameSpaceMap() {
        return nameSpaceMap;
    }

    /**
     * Get the handler for the general information on the schema
     *
     * @return the general information handler
     */
    public GeneralHandler getGeneralHandler() {
        return generalHandler;
    }

    /**
     * Get the handler for events on this schema
     *
     * @return the event handler
     */
    public EventHandler getEventHandler() {
        return eventHandler;
    }

    /**
     * Get the handler for parameters on this schema
     *
     * @return the parameters handler
     */
    public ParameterHandler getParameterHandler() {
        return parameterHandler;
    }

    /**
     * Get the handler for structures on this schema
     *
     * @return the structure handler
     */
    public StructureHandler getStructureHandler() {
        return structureHandler;
    }

    /**
     * Get the handler for categories on this schema
     *
     * @return the categories handler
     */
    public CategoryHandler getCategoryHandler() {
        return categoryHandler;
    }

    /**
     *
     * @return value
     */
    public String getParamPreamble() {
        return paramPreamble;
    }

    /**
     * @return value
     */
    public String getValuePreamble() {
        return valuePreamble;
    }

    /**
     * @return the packageName
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @return value
     */
    public String getAvroPackageName() {
        return avroPackageName;
    }

    /**
     * Render the schema object as a string
     *
     * @return a string representing the schema
     */
    @Override
    public final String toString() {
        return "Schema [name=" + name + "]";
    }

}

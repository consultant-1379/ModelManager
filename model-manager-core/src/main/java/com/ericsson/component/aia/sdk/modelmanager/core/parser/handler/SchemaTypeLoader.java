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

package com.ericsson.component.aia.sdk.modelmanager.core.parser.handler;

import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.SchemaUtil.DEFAULT_SCHEMA_TYPE_SCHEMA_FILE;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.SchemaUtil.SCHEMA_TYPE_NAMESPACE;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.SchemaUtil.SCHEMA_TYPE_XML_FILE;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.ResourceNotFoundException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.resource.ResourceFileFinder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.type.SchemaEnum;

/**
 * This class instantiates a SchemaTypeLoader instance to handle schema types, such as cell trace and EBM.
 *
 */

public class SchemaTypeLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaTypeLoader.class);

    private static final String SCHEMA_TYPE_NAMESPACE_PREFIX = "st";

    private static final String SCHEMA_TYPE_XPATH_EXPRESSION = "/st:schemaTypes/st:schemaType";

    private final Map<SchemaEnum, SchemaTypeHandler> schemaTypeMap = new TreeMap<SchemaEnum, SchemaTypeHandler>();

    /**
     * @throws SchemaException
     *             - exception
     * @throws ResourceNotFoundException
     *             - exception
     */
    public SchemaTypeLoader() throws SchemaException, ResourceNotFoundException {
        this(SCHEMA_TYPE_XML_FILE, DEFAULT_SCHEMA_TYPE_SCHEMA_FILE);
    }

    /**
     * @param document
     *            - document
     * @throws SchemaException
     *             - exception
     * @throws ResourceNotFoundException
     *             - exception
     * @throws JaxenException
     *             - exception
     */
    public SchemaTypeLoader(final Document document) throws SchemaException, ResourceNotFoundException, JaxenException {
        buildSchemaTypesMap(document);
    }

    /**
     * @param schemaTypeXmlFile
     *            location of SchemaType xml
     * @param schemaTypeXsdFile
     *            location of SchemaType xsd
     * @throws SchemaException
     *             - exception
     * @throws ResourceNotFoundException
     *             - exception
     */
    public SchemaTypeLoader(final String schemaTypeXmlFile, final String schemaTypeXsdFile) throws SchemaException, ResourceNotFoundException {
        LOGGER.debug("loading schemas . . .");
        final ResourceFileFinder resourceFileFinder = new ResourceFileFinder();

        final String schemaTypeSchemaFile = resourceFileFinder.getFileResourcePath(schemaTypeXsdFile);
        if (schemaTypeSchemaFile == null) {
            throw new SchemaException("failed to find schema type schema file configuration property: " + schemaTypeXsdFile);
        }

        final String xmlFile = resourceFileFinder.getFileResourcePath(schemaTypeXmlFile);
        if (xmlFile == null) {
            throw new SchemaException("failed to find schema type xml file configuration property: " + schemaTypeXmlFile);
        }

        LOGGER.debug("Schema type schema is at: {}", schemaTypeSchemaFile);
        LOGGER.debug("Schema type xml file is at: {}", schemaTypeXmlFile);

        try {
            final Map<String, String> schemaTypeSchemaMap = new HashMap<String, String>();
            schemaTypeSchemaMap.put(SCHEMA_TYPE_NAMESPACE, schemaTypeSchemaFile);

            final XMLDocumentHandler documentHandler = new XMLDocumentHandler(schemaTypeSchemaMap);

            final InputStream inputStream = resourceFileFinder.getFileResourceAsStream(schemaTypeXmlFile);

            final Document schemaTypeDocument = documentHandler.loadAndValidate(inputStream);

            buildSchemaTypesMap(schemaTypeDocument);

        } catch (final Exception e) {
            LOGGER.error("schemaType loading failed", e);
            throw new SchemaException("schema loading failed");
        }

        LOGGER.debug("schema loading completed");
    }

    private void buildSchemaTypesMap(final Document schemaTypeDocument) throws JaxenException, SchemaException {
        final Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put(SCHEMA_TYPE_NAMESPACE_PREFIX, SCHEMA_TYPE_NAMESPACE);

        final JDOMXPath xPathSchemaType = new JDOMXPath(SCHEMA_TYPE_XPATH_EXPRESSION);
        xPathSchemaType.setNamespaceContext(new SimpleNamespaceContext(nameSpaceMap));

        @SuppressWarnings("unchecked")
        final List<Element> schemaTypeNodeList = xPathSchemaType.selectNodes(schemaTypeDocument);

        for (final Element schemaTypeElement : schemaTypeNodeList) {
            final SchemaTypeHandler schemaTypeHandler = new SchemaTypeHandler(schemaTypeElement);
            schemaTypeMap.put(schemaTypeHandler.getName(), schemaTypeHandler);
        }
    }

    /**
     * Get the schema map for all schemas
     *
     * @return The schema NeTypes map
     */
    public Map<SchemaEnum, SchemaTypeHandler> getSchemaTypeMap() {
        return schemaTypeMap;
    }

}

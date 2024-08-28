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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * This class loads an XML document into a DOM and validates it
 */
public class XMLDocumentHandler {
    private static final String XERCES_SAX_PARSER = "org.apache.xerces.parsers.SAXParser";
    private static final String VALIDATION_FEATURE = "http://apache.org/xml/features/validation/schema";
    private static final String VALIDATION_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
    private static final String SCHEMA_LOCATION_PROPERTY = "http://apache.org/xml/properties/schema/external-schemaLocation";

    private final SAXBuilder builder;

    private Map<String, String> schemaURIMap = new HashMap<String, String>();

    /**
     * Constructor, set up a document handler with the appropriate schemas for validation
     *
     * @param schemaURIMap
     *            - schema
     */
    public XMLDocumentHandler(final Map<String, String> schemaURIMap) {
        builder = new SAXBuilder(XERCES_SAX_PARSER, true);
        builder.setFeature(VALIDATION_FEATURE, true);
        builder.setFeature(VALIDATION_FULL_CHECKING, true);

        setSchemas(schemaURIMap);
    }

    /**
     * Set the schemas for the XML document on this handler
     *
     * @param schemaURIMap
     *            - schema
     */
    private void setSchemas(final Map<String, String> schemaURIMap) {
        this.schemaURIMap = schemaURIMap;

        final StringBuilder schemaStringBuilder = new StringBuilder();
        for (final String schemaURI : this.schemaURIMap.keySet()) {
            schemaStringBuilder.append(schemaURI);
            schemaStringBuilder.append(' ');
            schemaStringBuilder.append(schemaURIMap.get(schemaURI));
            schemaStringBuilder.append(' ');
        }

        builder.setProperty(SCHEMA_LOCATION_PROPERTY, schemaStringBuilder.toString());
    }

    /**
     * Load the document into a DOM and validate it
     *
     * @param documentFile
     *            - document
     * @return the document as a DOM document
     * @throws IOException
     *             - exception
     * @throws JDOMException
     *             - exception
     */
    public Document loadAndValidate(final File documentFile) throws JDOMException, IOException {
        return builder.build(documentFile);
    }

    /**
     * Load the document into a DOM and validate it
     *
     * @param documentStream
     *            The document as a stream
     * @return the document as a DOM document
     * @throws IOException
     *             - exception
     * @throws JDOMException
     *             - exception
     */
    public Document loadAndValidate(final InputStream documentStream) throws JDOMException, IOException {
        return builder.build(documentStream);
    }
}

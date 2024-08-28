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

import java.io.IOException;

import org.jaxen.JaxenException;
import org.jdom.Document;
import org.jdom.JDOMException;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;

/**
 * This interface enforces implementation of map building for schema components from an XML file. It also gets table definitions and table rows for
 * component meta data
 */
public abstract class SchemaComponentHandler {
    protected Schema schema = null;

    /**
     * Constructor to initialize the handler for this schema
     *
     * @param schema
     *            The schema for which this map is being built
     */
    public SchemaComponentHandler(final Schema schema) {
        this.schema = schema;
    }

    /**
     * Builds a map from elements in the XML file.
     *
     * @param mapDocument
     *            the XML document
     * @throws JaxenException
     *             - exception
     * @throws IOException
     *             - exception
     * @throws JDOMException
     *             - exception
     * @throws SchemaException
     *             - exception
     */
    public abstract void buildMap(Document mapDocument) throws SchemaException, JDOMException, IOException, JaxenException;

    /**
     * Return the event schema used by this handler
     *
     * @return the event schema
     */
    public Schema getSchema() {
        return schema;
    }
}

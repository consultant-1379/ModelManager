/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.sdk.modelmanager.core.parser.model;

import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.SchemaUtil.EMPTY_STRING;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.SchemaUtil.EVENT_SCHEMA_XSD_FILE_LOCATION;

import java.io.IOException;

import org.jaxen.JaxenException;
import org.jdom.JDOMException;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.resource.ResourceFileFinder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.resource.ResourceProvider;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.type.SchemaEnum;

/**
 * @author aia
 */
public final class SchemaBuilder {

    private final SchemaEnum name;

    private String paramPreamble = EMPTY_STRING;

    private String valuePreamble = EMPTY_STRING;

    private String schemaXsdFile = EVENT_SCHEMA_XSD_FILE_LOCATION;

    private final String schemaXMLFile;

    private ResourceProvider resourceProvider = new ResourceFileFinder();

    /**
     * Default
     *
     * @param name
     *            - name
     * @param schemaXMLFile
     *            - file
     */
    public SchemaBuilder(final SchemaEnum name, final String schemaXMLFile) {
        this.name = name;
        this.schemaXMLFile = schemaXMLFile;
    }

    /**
     * @param paramPreamble
     *            Parameter preamble (text to trim in Event parameter's name i.e. <b>EVENT_PARAM_</b> will trim EVENT_PARAM_TIMESTAMP_HOUR parameter
     *            to TIMESTAMP_HOUR)
     * @return schema
     */
    public SchemaBuilder paramPreamble(final String paramPreamble) {
        this.paramPreamble = paramPreamble;
        return this;
    }

    /**
     * @param valuePreamble
     *            Value preamble (text to trim in enumeration value's name i.e <b>EVENT_VALUE</b> will trim EVENT_VALUE_SUCCESS to SUCCESS)
     * @return schema
     */
    public SchemaBuilder valuePreamble(final String valuePreamble) {
        this.valuePreamble = valuePreamble;
        return this;
    }

    /**
     * @param schemaXsdFile
     *            location of Schema xml file (i.e. 101/ schmea or Pm Schema)
     * @return schema
     */
    public SchemaBuilder schemaXsdFile(final String schemaXsdFile) {
        this.schemaXsdFile = schemaXsdFile;
        return this;
    }

    /**
     * @param resourceProvider
     *            - resource
     * @return schema
     */
    public SchemaBuilder resourceProvider(final ResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
        return this;
    }

    /**
     * @return the name
     */
    public SchemaEnum getName() {
        return name;
    }

    public String getParamPreamble() {
        return paramPreamble;
    }

    public String getValuePreamble() {
        return valuePreamble;
    }

    public String getSchemaXsdFile() {
        return schemaXsdFile;
    }

    public String getSchemaXMLFile() {
        return schemaXMLFile;
    }

    public ResourceProvider getResourceProvider() {
        return resourceProvider;
    }

    /**
     * @return schema
     * @throws JaxenException
     *             - exception
     * @throws SchemaException
     *             - exception
     * @throws JDOMException
     *             - exception
     * @throws IOException
     *             - exception
     */
    public Schema build() throws JaxenException, SchemaException, JDOMException, IOException {
        return new Schema(this);
    }
}

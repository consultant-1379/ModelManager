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

import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.SchemaUtil.EMPTY_STRING;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.SchemaUtil.SCHEMA_FILE_EXTENSION;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.SchemaUtil.SCHEMA_TYPE_NAMESPACE;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.Element;
import org.jdom.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.type.SchemaEnum;

/**
 * This class handles schema administration for schemas of a particular type in xStream. It loads all schemas of a particular type into memory from
 * XML files, uses the rules below to resolve all those schemas into a single event parameter map for the given schema type. A single instance of this
 * class exists for all Cell Trace, EBM, or CTUM schemas. The mapping rules used are: 1) Events are added to the event map for the most recent version
 * first working back to the least recent version 2) If an event does not exist in the latest schema version but appears in an earlier schema version,
 * it is added to the map 3) Parameters are added to the map from the latest version backwards; a parameter will always have the attributes from the
 * latest event schema in which it appears. Therefore, if, for example, the length of a parameter is increased in a later version, the increased
 * length will be the length of the parameter in the map. It is assumed that parameters will never get more restrictive definitions in later versions
 * 4) If a parameter is dropped from an event in later versions, it will appear in the map.
 */
public class SchemaTypeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaTypeHandler.class);

    private SchemaEnum name;

    private int idLength;

    private int idStartPos;

    private boolean idInEvent;

    private String paramPreamble;

    private String valuePreamble;

    private final Set<String> xmlFilesSet = new HashSet<String>();

    /**
     * @param schemaTypeElement
     *            jdom element for schematype tag
     * @throws SchemaException
     *             - exception
     */
    protected SchemaTypeHandler(final Element schemaTypeElement) throws SchemaException {

        final Namespace schemaTypeNamespace = Namespace.getNamespace(SCHEMA_TYPE_NAMESPACE);

        try {
            name = SchemaEnum.fromValue(schemaTypeElement.getChild("name", schemaTypeNamespace).getText().trim());
            idLength = Integer.valueOf(schemaTypeElement.getChild("idLength", schemaTypeNamespace).getText().trim());
            idStartPos = Integer.valueOf(schemaTypeElement.getChild("idStartPos", schemaTypeNamespace).getText().trim());
            idInEvent = Boolean.valueOf(schemaTypeElement.getChild("idInEvent", schemaTypeNamespace).getText().trim());

            final Element parmPreambleElement = schemaTypeElement.getChild("paramPreamble", schemaTypeNamespace);
            paramPreamble = parmPreambleElement != null ? parmPreambleElement.getText().trim() : EMPTY_STRING;

            final Element valuePreambleElement = schemaTypeElement.getChild("valuePreamble", schemaTypeNamespace);
            valuePreamble = valuePreambleElement != null ? valuePreambleElement.getText().trim() : EMPTY_STRING;

            final Element xmlFilesElement = schemaTypeElement.getChild("xmlFiles", schemaTypeNamespace);
            if (xmlFilesElement != null) {
                @SuppressWarnings("unchecked")
                final List<Element> pathsElementList = xmlFilesElement.getChildren("path", schemaTypeNamespace);
                if (pathsElementList != null) {
                    for (final Element pathElement : pathsElementList) {
                        final String xmlFilePath = pathElement.getText();
                        if (isValidPath(xmlFilePath)) {
                            xmlFilesSet.add(xmlFilePath.trim());
                        }
                    }
                }
            }
        } catch (final Exception e) {
            LOGGER.warn("loading of a schema in schema type " + name + " failed", e);
        }
    }

    private boolean isValidPath(final String filePath) {
        return filePath != null && filePath.endsWith(SCHEMA_FILE_EXTENSION);
    }

    /**
     * Get the schema type name
     *
     * @return The schema type name
     */
    public SchemaEnum getName() {
        return name;
    }

    /**
     * Get the ID length for this schema type
     *
     * @return The ID length in bits
     */
    public int getIdLength() {
        return idLength;
    }

    /**
     * Get the ID start position bit for this schema type
     *
     * @return The ID start position bit
     */
    public int getIdStartPos() {
        return idStartPos;
    }

    /**
     * Find if the event ID is in the event as a field
     *
     * @return True if the event ID is a field in the event
     */
    public boolean isIdInEvent() {
        return idInEvent;
    }

    /**
     * Get the parameter preamble for parameters on this schema type
     *
     * @return the parameter preamble
     */
    public String getParamPreamble() {
        return paramPreamble;
    }

    /**
     * Get the value preamble for parameters on this schema type
     *
     * @return the value preamble
     */
    public String getValuePreamble() {
        return valuePreamble;
    }

    /**
     * @return the xmlFilesSet
     */
    public Set<String> getXmlFilesSet() {
        return xmlFilesSet;
    }
}

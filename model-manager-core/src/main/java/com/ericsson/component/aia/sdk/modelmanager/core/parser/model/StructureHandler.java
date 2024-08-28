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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;

/**
 * This class is used to hold parameter structures
 */
public class StructureHandler extends SchemaComponentHandler {
    protected static final String STRUCTURE_TABLE_NAME = "structures";

    private static final String STRUCTURE_XPATH_EXPRESSION = "/pe:eventspecification/pe:structuretypes/pe:structuretype";

    private final Map<String, Structure> structureMap;

    /**
     * Constructor to initialize the handler for this schema
     *
     * @param schema
     *            The schema for which this map is being built
     */
    public StructureHandler(final Schema schema) {
        super(schema);

        structureMap = new LinkedHashMap<String, Structure>();
    }

    /**
     * Builds the structure map from elements in the XML file.
     *
     * @param schema
     *            A string indicating the schema for which this map is being built
     * @param eventDocument
     *            the event XML document
     * @param nameSpaceMap
     *            the name spaces used in the XML document
     * @throws SchemaException
     * @throws JaxenException
     */
    @Override
    public void buildMap(final Document eventDocument) throws SchemaException, JaxenException {
        final JDOMXPath xPathStructure = new JDOMXPath(STRUCTURE_XPATH_EXPRESSION);
        xPathStructure.setNamespaceContext(new SimpleNamespaceContext(schema.getNameSpaceMap()));

        @SuppressWarnings("unchecked")
        final List<Element> structureNodeList = xPathStructure.selectNodes(eventDocument);

        for (final Element structureElement : structureNodeList) {
            final Structure currentStructure = new Structure(this, structureElement, schema.getNameSpace());

            structureMap.put(currentStructure.getName(), currentStructure);
        }
    }

    /**
     * Get the structure mapping
     *
     * @return The structure mapping
     */
    public Map<String, Structure> getMap() {
        return structureMap;
    }
}

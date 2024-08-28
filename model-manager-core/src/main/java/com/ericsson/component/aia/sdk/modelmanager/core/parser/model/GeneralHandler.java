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
 * This class holds general information about a schema
 */
public class GeneralHandler extends SchemaComponentHandler {
    private static final String GENERAL_XPATH_EXPRESSION = "/pe:eventspecification/pe:general";

    private final Map<Integer, General> generalMap;

    /**
     * Constructor to initialize the handler for this schema
     *
     * @param schema
     *            The schema for which this map is being built
     */
    public GeneralHandler(final Schema schema) {
        super(schema);

        generalMap = new LinkedHashMap<Integer, General>();
    }

    /**
     * Builds the general map from elements in the XML file.
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
        final JDOMXPath xPathGeneral = new JDOMXPath(GENERAL_XPATH_EXPRESSION);
        xPathGeneral.setNamespaceContext(new SimpleNamespaceContext(schema.getNameSpaceMap()));

        @SuppressWarnings("unchecked")
        final List<Element> generalNodeList = xPathGeneral.selectNodes(eventDocument);

        final int generalElementNo = 0;
        General currentGeneral;
        for (final Element generalElement : generalNodeList) {
            currentGeneral = new General(generalElement, schema.getNameSpace());

            generalMap.put(generalElementNo, currentGeneral);
        }
    }

    /**
     * Get the general mapping
     *
     * @return The general mapping
     */
    public Map<Integer, General> getMap() {
        return generalMap;
    }

    /**
     * Get the first general information element
     *
     * @return The first general information element
     */
    public General getGeneralInfo() {
        return generalMap.get(0);
    }
}

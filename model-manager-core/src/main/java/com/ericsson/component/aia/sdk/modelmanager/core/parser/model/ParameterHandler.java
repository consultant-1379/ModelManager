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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;

/**
 * This class holds the definitions of all parameters in a schema
 */
public class ParameterHandler extends SchemaComponentHandler {
    protected static final String PARAMETER_TYPES_TABLE_NAME = "param_types";

    protected static final String PARAMETER_ENUMS_TABLE_NAME = "param_enums";

    private static final String PARAMETER_TYPE_XPATH_EXPRESSION = "/pe:eventspecification/pe:parametertypes/pe:parametertype";

    private final Map<String, Parameter> parameterMap;

    private final String schemaType;

    /**
     * Constructor to initialize the parameter handler for this schema
     *
     * @param schema
     *            A string indicating the schema for which this map is being built
     * @param schemaType
     *            - type
     */
    public ParameterHandler(final Schema schema, final String schemaType) {
        super(schema);
        parameterMap = new LinkedHashMap<String, Parameter>();
        this.schemaType = schemaType;
    }

    /**
     * Builds the parameter map from elements in an XMP file.
     *
     * @param eventDocument
     *            the event XML document
     * @param nameSpaceMap
     *            the name spaces used in the XML document
     * @throws SchemaException
     * @throws IOException
     * @throws JDOMException
     * @throws JaxenException
     */
    @Override
    public void buildMap(final Document eventDocument) throws SchemaException, JDOMException, IOException, JaxenException {

        final JDOMXPath xPathPar = new JDOMXPath(PARAMETER_TYPE_XPATH_EXPRESSION);
        xPathPar.setNamespaceContext(new SimpleNamespaceContext(schema.getNameSpaceMap()));

        @SuppressWarnings("unchecked")
        final List<Element> parNodeList = xPathPar.selectNodes(eventDocument);

        for (final Element parameter : parNodeList) {
            final Parameter currentParameter = new Parameter(parameter, schema.getNameSpace(), schema.getParamPreamble(), schema.getValuePreamble(),
                    schemaType);

            parameterMap.put(currentParameter.getName(), currentParameter);
        }
    }

    /**
     * Get the parameter mapping
     *
     * @return The parameter mapping
     */
    public Map<String, Parameter> getMap() {
        return parameterMap;
    }
}

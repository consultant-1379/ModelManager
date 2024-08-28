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
 * This class holds event categories
 */
public class CategoryHandler extends SchemaComponentHandler {
    protected static final String CATEGORY_TABLE_NAME = "event_categories";

    private static final String CATEGORY_XPATH_EXPRESSION = "/pe:eventspecification/pe:eventcategories/pe:eventcategory";

    private final Map<String, Category> categoryMap;

    /**
     * Constructor to initialize the handler for this schema
     *
     * @param schema
     *            The schema for which this map is being built
     */
    public CategoryHandler(final Schema schema) {
        super(schema);

        categoryMap = new LinkedHashMap<String, Category>();
    }

    /**
     * Builds the category map from elements in the XML file.
     *
     * @param eventDocument
     *            the event XML document
     * @throws SchemaException
     * @throws JaxenException
     */
    @Override
    public void buildMap(final Document eventDocument) throws SchemaException, JaxenException {
        final JDOMXPath xPathCategory = new JDOMXPath(CATEGORY_XPATH_EXPRESSION);
        xPathCategory.setNamespaceContext(new SimpleNamespaceContext(schema.getNameSpaceMap()));

        @SuppressWarnings("unchecked")
        final List<Element> categoryNodeList = xPathCategory.selectNodes(eventDocument);

        for (final Element categoryElement : categoryNodeList) {
            final Category currentCategory = new Category(this, categoryElement, schema.getNameSpace());

            categoryMap.put(currentCategory.getName(), currentCategory);
        }
    }

    /**
     * Get the category mapping
     *
     * @return The category mapping
     */
    public Map<String, Category> getMap() {
        return categoryMap;
    }
}

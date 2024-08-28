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

import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;

/**
 * This class is used to hold a single structure type
 */
public class Structure implements Comparable<Structure> {
    private final String name;

    private final List<EventParameter> parameterList;

    /**
     * Instantiate a structure as an object
     *
     * @param handler
     *            The handler for this structure, used to look up parameters
     * @param structure
     *            The structure XML element
     * @param namespace
     *            the name space to use for reading elements
     * @throws SchemaException
     *             - SchemaException
     */
    protected Structure(final SchemaComponentHandler handler, final Element structure, final Namespace namespace) throws SchemaException {
        name = structure.getChild("name", namespace).getText().trim();

        @SuppressWarnings("unchecked")
        final List<Element> children = structure.getChild("elements", namespace).getChildren();

        try {
            parameterList = EventParameter.getParameters(handler, children);
        } catch (final Exception e) {
            throw new SchemaException("Error parsing event schema element [" + structure.getName() + "=" + name + "] " + e);
        }
    }

    /**
     * Get the name of this structure
     *
     * @return The structurename
     */
    public String getName() {
        return name;
    }

    /**
     * Get the parameter values
     *
     * @return the ENUM values
     */
    public List<EventParameter> getParameterList() {
        return parameterList;
    }

    /**
     * Method to dump the structure to a string
     *
     * @return string containing the parameter
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        builder.append(name);

        return builder.toString();
    }

    /**
     * Compare this event to another event, comparison based on name
     *
     * @param comparedEvent
     *            - comparedEvent
     * @return the comparison value
     */
    @Override
    public int compareTo(final Structure comparedEvent) {
        return name.compareTo(comparedEvent.name);
    }

    /**
     * Override the equals method for hash maps of structures
     */
    @Override
    public boolean equals(final Object other) {
        if (other instanceof Structure) {
            final Structure otherStructure = (Structure) other;
            return name.equals(otherStructure.getName());
        }
        return false;
    }

    /**
     * Override the hashCode hash maps of structures
     *
     * @return The hash code of the structure name
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

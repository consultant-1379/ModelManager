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

import java.util.ArrayList;
import java.util.List;

import org.jdom.DataConversionException;
import org.jdom.Element;
import org.jdom.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;

/**
 * This class is used to hold a single event category
 */
public class Category implements Comparable<Category> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Category.class);

    private final String name;

    private final List<Event> eventList = new ArrayList<Event>();

    /**
     * Instantiate a category as an object
     *
     * @param handler
     *            the category handler for this category
     * @param category
     *            the category XML element
     * @param namespace
     *            the name space to use for reading elements
     * @throws SchemaException
     *             - exception
     */
    protected Category(final SchemaComponentHandler handler, final Element category, final Namespace namespace) throws SchemaException {
        name = category.getChild("name", namespace).getText().trim();
        @SuppressWarnings("unchecked")
        final List<Element> eventNodeList = category.getChildren("eventref", namespace);

        try {
            loadEvents(handler, eventNodeList);
        } catch (final Exception e) {
            throw new SchemaException("Error parsing event schema element [" + category.getName() + "=" + name + "] " + e);
        }
    }

    /**
     * Set the event list for this category
     *
     * @param handler
     *            the category handler for this category
     * @param elementNode
     *            The list of event XML elements
     * @throws DataConversionException
     */
    private void loadEvents(final SchemaComponentHandler handler, final List<Element> eventNodeList) throws DataConversionException {
        for (final Element eventNode : eventNodeList) {
            final String eventName = eventNode.getAttribute("name").getValue().trim();

            final Integer eventId = eventNode.getAttribute("id").getIntValue();

            final Event event = handler.getSchema().getEventHandler().getMap().get(eventId);

            if (event != null && event.getName().equalsIgnoreCase(eventName)) {
                eventList.add(event);
            } else {
                LOGGER.warn("Unknown event \"" + eventName + "\" specified on category");
            }
        }
    }

    /**
     * Get the name of the category
     *
     * @return The category name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the event list
     *
     * @return the event list
     */
    public List<Event> getEventList() {
        return eventList;
    }

    /**
     * Method to dump the category to a string
     *
     * @return string containing the category
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        builder.append(name);
        builder.append(",");

        for (final Event event : eventList) {
            builder.append("[");
            builder.append(event.getName());
            builder.append("]");
        }

        return builder.toString();
    }

    /**
     * Compare this category to another category, comparison based on name
     *
     * @param comparedEventCategory
     * @return the comparison value
     */
    @Override
    public int compareTo(final Category comparedEvCat) {
        return name.compareTo(comparedEvCat.name);
    }

    /**
     * Override the equals method for hash maps of categories
     */
    @Override
    public boolean equals(final Object other) {
        if (other instanceof Category) {
            final Category otherCategory = (Category) other;
            return name.equals(otherCategory.getName());
        }
        return false;
    }

    /**
     * Override the hashCode hash maps of categories
     *
     * @return The hash code of the category name
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

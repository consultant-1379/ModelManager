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
package com.ericsson.component.aia.sdk.modelmanager.core.parser.util;

import java.util.LinkedHashSet;
import java.util.Set;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.Event;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.EventParameter;

/**
 * This class holds a mapped event, consisting of an event reference, a list of parameter references, and a list of extra column references that
 * represent an event mapped for all versions in a schema
 */
public class MappedEvent implements Comparable<MappedEvent> {

    private final Event event;

    private final Set<EventParameter> parameterSet = new LinkedHashSet<EventParameter>();

    /**
     *
     * @param event
     *            - event
     */
    public MappedEvent(final Event event) {
        this.event = event;

    }

    /**
     * Set the parameters from this event onto the event mapping if they have not already been set
     *
     * @param event
     *            The event to use when reading parameters
     */
    public void setParameters(final Event event) {
        parameterSet.addAll(event.getParameterList());
    }

    /**
     * Get the event for this event map
     *
     * @return The event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Get the parameters for this map
     *
     * @return The mapped parameters
     */
    public Set<EventParameter> getParameterSet() {
        return parameterSet;
    }

    /**
     * Compare two mapped event objects
     *
     * @param The
     *            mapped event to compare this mapped event to
     * @return The comparison result
     */
    @Override
    public int compareTo(final MappedEvent mappedEvent) {
        return this.event.compareTo(mappedEvent.event);
    }
}

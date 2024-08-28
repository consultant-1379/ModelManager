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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.DataConversionException;
import org.jdom.Element;
import org.jdom.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.GenericParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;

/**
 * This class is used to hold a single event type
 *
 */
@SuppressWarnings("PMD")
/**
 * @author aia
 */
public class Event implements Comparable<Event> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Event.class);

    private static final Map<String, String> parameterNameToRenameMap = new HashMap<>();

    private String name;

    private final int identifier;

    private Category category;

    private int length;

    private boolean variableLength;

    private boolean bitPacked;

    private List<EventParameter> parameterList = new ArrayList<EventParameter>();

    static {
        parameterNameToRenameMap.put("L_HEADER_TIME_MILLISECOND", "TIME_MILLISECOND");
        parameterNameToRenameMap.put("L_HEADER_HEADER_TIME_MINUTE", "HEADER_TIME_MINUTE");
        parameterNameToRenameMap.put("L_HEADER_HEADER_TIME_SECOND", "HEADER_TIME_SECOND");
        parameterNameToRenameMap.put("L_HEADER_HEADER_TIME_HOUR", "HEADER_TIME_HOUR");
        parameterNameToRenameMap.put("L_HEADER_HEADER_EVENT_ID", "HEADER_EVENT_ID");
        parameterNameToRenameMap.put("L_HEADER_HEADER_EVENT_RESULT", "HEADER_EVENT_RESULT");
        parameterNameToRenameMap.put("L_HEADER_DURATION", "DURATION");
    }

    /**
     * Instantiate an event as an object
     *
     * @param handler
     *            The event handler for this event, used to look up parameters
     * @param event
     *            The event XML element
     * @param namespace
     *            - namespace
     * @throws SchemaException
     *             - exception
     */
    protected Event(final SchemaComponentHandler handler, final Element event, final Namespace namespace) throws SchemaException {
        try {
            this.name = event.getChild("name", namespace).getText().trim();
            this.identifier = Integer.valueOf(event.getChild("id", namespace).getText().trim());
            this.parameterList = loadParameters(handler, event.getChild("elements", namespace));
        } catch (final Exception exception) {
            throw new SchemaException("Error parsing event schema element [" + event.getName() + "=" + name + "] " + exception);
        }
    }

    /**
     * Set the parameter list for this event
     *
     * @param handler
     *            The event handler for this event, used to look up parameters
     * @param elementNode
     *            The element XML element that has parameters as children
     * @throws SchemaException
     * @throws DataConversionException
     */
    private List<EventParameter> loadParameters(final SchemaComponentHandler handler, final Element elementNode)
            throws SchemaException, DataConversionException {
        @SuppressWarnings("unchecked")
        final List<Element> children = elementNode.getChildren();

        List<EventParameter> eventParameters = EventParameter.getParameters(handler, children);

        bitPacked = eventParameters.get(0).getParameter().isBitPacked();
        final String firstParamName = eventParameters.get(0).getParameter().getName();

        int parPosition = 0;

        for (final EventParameter parameter : eventParameters) {
            if (parameter.getParameter().isBitPacked() != bitPacked) {
                final String paramName = parameter.getParameter().getName();
                throw new SchemaException("Mixed bit packed and non bit packed parameters specified on event: " + name
                        + ". Difference between parameters '" + firstParamName + "' and '" + paramName + "'");
            }

            if (parameter.isVariableLength() || parameter.isOptional() || parameter.isStructArray()) {
                variableLength = true;
            }

            if (parameter.isStructArray()) {
                length += parameter.getNumberOfBytes() * parameter.getMaxStructArraySize();
                int structFirstParamPos = parPosition;
                while (!eventParameters.get(structFirstParamPos).isStructFirstParameter()) {
                    structFirstParamPos--;
                }
                parameter.setStartSkip(calculateStartSkip(eventParameters, parPosition, parameter));
                parameter.setEndSkip(calculateEndSkip(eventParameters, parPosition, parameter));
                parameter.setStructSize(calculateStructSize(eventParameters, structFirstParamPos));
            } else {
                length += parameter.getNumberOfBytes();
                length += getUseValidFlagLength(parameter);
                length += bitPacked && parameter.isOptional() ? 1 : 0;
            }
            parPosition++;
        }

        if (bitPacked) {
            padLengthToNext32BitBoundary();
        }

        eventParameters = eliminateDuplicateParameter(eventParameters);
        renameParameterNames(eventParameters);

        if (length == -1) {
            LOGGER.error("Name:  " + name + "---Size:  " + length);
        }
        return eventParameters;
    }

    private int getUseValidFlagLength(final EventParameter parameter) {
        return parameter.isUseValid() && !parameter.isValidLTEembeddedbitFlag() ? 1 : 0;
    }

    private List<EventParameter> eliminateDuplicateParameter(final List<EventParameter> parameterList) {
        final List<EventParameter> uniqueParameters = new ArrayList<>();
        final Set<String> uniqueParameterNames = new LinkedHashSet<>();

        for (int index = 0; index < parameterList.size(); index++) {
            final EventParameter currentEventParameter = parameterList.get(index);
            final String name = currentEventParameter.getName();
            if (!uniqueParameterNames.contains(name)) {
                uniqueParameterNames.add(name);
                uniqueParameters.add(currentEventParameter);

                final int duplicateParameterCount = getDuplicateCount(parameterList, index, name);

                if (duplicateParameterCount > 0) {
                    LOGGER.trace("Event: '{}', Parameter: '{}' at index: '{}' is duplicated '{}' times", this.name, name, index,
                            duplicateParameterCount);
                    currentEventParameter.setEventArrayParameter(true);
                    currentEventParameter.setEventArrayParameterSize(duplicateParameterCount + 1);
                    final GenericParameterDecoder genericParameterDecoder = currentEventParameter.getParserType()
                            .getEventArrayByteParameterDecoder(currentEventParameter);
                    currentEventParameter.setParameterDecoder(genericParameterDecoder);
                }
            }
        }
        return uniqueParameters;
    }

    /**
     * Rename Parameter Names
     *
     * @param eventParameters
     */
    private void renameParameterNames(final List<EventParameter> eventParameters) {
        for (final EventParameter eventParameter : eventParameters) {
            final String name = eventParameter.getName();
            if (parameterNameToRenameMap.containsKey(name)) {
                eventParameter.setName(parameterNameToRenameMap.get(name));
            }
        }
    }

    private int getDuplicateCount(final List<EventParameter> parameterList, final int index, final String name) {
        int duplicateParameterCount = 0;
        for (int parameterIndex = index + 1; parameterIndex < parameterList.size(); parameterIndex++) {
            final EventParameter eventParameter = parameterList.get(parameterIndex);
            if (name.equalsIgnoreCase(eventParameter.getName())) {
                duplicateParameterCount++;
            }
        }
        return duplicateParameterCount;
    }

    private void padLengthToNext32BitBoundary() {
        if (length % 32 > 0) {
            length += 32 - length % 32;
        }
    }

    /**
     * Get the name of an event
     *
     * @return The event name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the ID of an event
     *
     * @return The event ID
     */
    public int getId() {
        return identifier;
    }

    /**
     * returns true if this event is bit packed
     *
     * @return true if this event is bit packed
     */
    public boolean isBitpacked() {
        return bitPacked;
    }

    /**
     * Get the total length of this record
     *
     * @return The record length
     */
    public int getLength() {
        return length;
    }

    /**
     * Check if this event is of variable length
     *
     * @return true if the record is of variable length
     */
    public boolean isVariableLength() {
        return variableLength;
    }

    /**
     *
     * @return the parameter list
     */
    public List<EventParameter> getParameterList() {
        return parameterList;
    }

    /**
     * Get the category of this event
     *
     * @return the category of the event
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Set the category of this event
     *
     * @param category
     *            the category of the event
     */
    public void setCategory(final Category category) {
        this.category = category;
    }

    /**
     * Method to dump the event to a string
     *
     * @return string containing the parameter
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        builder.append(name);
        builder.append(",");
        builder.append(identifier);
        builder.append(",");
        builder.append(category);

        for (final EventParameter parameter : parameterList) {
            builder.append("[");
            builder.append(parameter.toString());
            builder.append("]");
        }

        return builder.toString();
    }

    /**
     * Compare this event to another event, comparison based on name
     *
     * @param comparedEvent
     * @return the comparison value
     */
    @Override
    public int compareTo(final Event comparedEvent) {
        return name.compareTo(comparedEvent.name);
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof Event) {
            final Event otherEvent = (Event) other;
            return name.equals(otherEvent.getName());
        }
        return false;
    }

    /**
     * @return The hash code of the event name
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * @param eventParameterList
     *            - event
     * @param parPostion
     *            - position
     * @param parameter
     *            - parameter
     * @return start skip
     */
    public static int calculateStartSkip(final List<EventParameter> eventParameterList, final int parPostion, final EventParameter parameter) {
        int startSkip = 0;
        if (!parameter.isStructFirstParameter()) {
            for (int firstPar = parPostion - 1; firstPar >= 0; firstPar--) {
                final EventParameter eventParameter = eventParameterList.get(firstPar);
                startSkip += getNumberOfBytesWithValidtyByte(eventParameter);
                if (eventParameterList.get(firstPar).isStructFirstParameter()) {
                    break;
                }
            }
        }
        return startSkip;
    }

    private static int getNumberOfBytesWithValidtyByte(final EventParameter eventParameter) {
        int bytes = eventParameter.getNumberOfBytes();
        bytes += ((eventParameter.isUseValid() || eventParameter.isOptional()) && !eventParameter.isValidLTEembeddedbitFlag()) ? 1 : 0;
        return bytes;
    }

    /**
     * @param eventParameterList
     *            - event
     * @param parPosition
     *            - position
     * @param parameter
     *            -parameter
     * @return end skip
     */
    public static int calculateEndSkip(final List<EventParameter> eventParameterList, final int parPosition, final EventParameter parameter) {
        int endSkip = 0;
        if (!parameter.isStructLastParameter()) {
            for (int lastPar = parPosition + 1; lastPar < eventParameterList.size(); lastPar++) {
                final EventParameter eventParameter = eventParameterList.get(lastPar);
                endSkip += getNumberOfBytesWithValidtyByte(eventParameter);
                if (eventParameter.isStructLastParameter()) {
                    break;
                }
            }
        }
        return endSkip;
    }

    private int calculateStructSize(final List<EventParameter> eventParameterList, final int parPosition) {
        int structSize = 0;
        for (int structPar = parPosition; structPar < eventParameterList.size(); structPar++) {
            final EventParameter eventParameter = eventParameterList.get(structPar);
            structSize += getNumberOfBytesWithValidtyByte(eventParameter);
            if (eventParameterList.get(structPar).isStructLastParameter()) {
                break;
            }
        }
        return structSize;
    }
}

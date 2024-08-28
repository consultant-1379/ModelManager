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
import java.util.Comparator;
import java.util.List;

import org.jdom.DataConversionException;
import org.jdom.Element;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.GenericParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParameterDecoderFactory;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParserType;

/**
 * This class holds information on a specific parameter on an event. An event can have more than one instance of a particular parameter type so this
 * class holds those instances. It also holds mapping information for parameters to table columns.
 *
 */
public class EventParameter implements Comparable<EventParameter>, Comparator<EventParameter> {

    public static final int NOT_SET = -1;

    private String name = "";

    private final Parameter parameter;

    private final String type;

    private final ParserType parserType;

    private int numberOfBytes;

    private final int numberOfBits;

    private final boolean variableLength;

    private boolean useValid;

    private boolean optional;

    private final boolean validLTEembeddedbitFlag;

    private final StructureArrayParam structureArrayParam = new StructureArrayParam();

    private GenericParameterDecoder parameterDecoder = null;

    private boolean eventArrayParameter;

    private int eventArrayParameterSize = 0;

    private EventParameter(final Parameter parameter) throws SchemaException {
        this.parameter = parameter;
        this.name = parameter.getName();
        this.type = parameter.getType();
        this.parserType = parameter.getParserType();
        this.numberOfBytes = parameter.getNumberOfBytes();
        this.numberOfBits = parameter.getNumberOfBits();
        this.variableLength = parameter.isVariableLength();
        this.useValid = parameter.isUseValid();
        this.validLTEembeddedbitFlag = parameter.isValidLTEembeddedbitFlag();
    }

    /**
     * This method returns a list of parameters from a list of XML elements containing struct and param elements
     *
     * @param handler
     *            A SchemaHandler used to look up parameters
     * @param children
     *            The list of XML elements
     * @return events
     * @throws SchemaException
     *             - exception
     * @throws DataConversionException
     *             - exception
     */
    protected static List<EventParameter> getParameters(final SchemaComponentHandler handler, final List<Element> children)
            throws DataConversionException, SchemaException {
        final List<EventParameter> parameterList = new ArrayList<EventParameter>();

        for (final Element child : children) {
            if (child.getName().equals("param")) {
                parameterList.add(getSingleParameter(handler, child));
            } else if (child.getName().equals("struct")) {
                parameterList.addAll(getStructParameters(handler, child));
            }
        }

        return parameterList;
    }

    /**
     * This method creates a new parameter type object from a param XML element in the event schema file
     *
     * @param handler
     *            A schemahandler to look up parameter types
     * @param structElement
     *            The struct XML element
     * @return returns a list of EventParam objects describing the structure instance
     * @throws SchemaException
     * @throws DataConversionException
     */
    private static List<EventParameter> getStructParameters(final SchemaComponentHandler handler, final Element structElement)
            throws SchemaException, DataConversionException {
        final String structureType = structElement.getAttribute("type").getValue();

        String structureName = structureType;

        if (structElement.getText() != null && structElement.getText().trim().length() > 0) {
            structureName = structElement.getText().trim();
        }

        final Structure structure = handler.getSchema().getStructureHandler().getMap().get(structureType);

        if (structure == null) {
            throw new SchemaException("Unknown structure \"" + structureType + "\" specified on event");
        }

        boolean structArray = false;
        int maxStructArraySize = 0;
        if (structElement.getAttribute("seqmaxlen") != null) {
            structArray = true;
            maxStructArraySize = structElement.getAttribute("seqmaxlen").getIntValue();
        }

        final int size = structure.getParameterList().size();
        return getStructParameters(structureName, structure, structArray, maxStructArraySize, size);

    }

    private static List<EventParameter> getStructParameters(final String structureName, final Structure structure, final boolean structArray,
                                                            final int maxStructArraySize, final int size)
            throws SchemaException {

        final List<EventParameter> structureParameterList = new ArrayList<EventParameter>(size);
        final List<EventParameter> structParameterList = structure.getParameterList();

        for (int i = 0; i < structParameterList.size(); i++) {
            final EventParameter newEventParameter = new EventParameter(structParameterList.get(i).parameter);

            newEventParameter.name = structureName + '_' + structParameterList.get(i).name;

            newEventParameter.useValid = structParameterList.get(i).useValid;
            newEventParameter.optional = structParameterList.get(i).optional;

            newEventParameter.structureArrayParam.setStructArray(structArray);
            newEventParameter.structureArrayParam.setMaxStructArraySize(maxStructArraySize);
            newEventParameter.structureArrayParam.setValidStructureArraySize(maxStructArraySize);

            newEventParameter.parameterDecoder = ParameterDecoderFactory.getParameterDecoder(newEventParameter);

            if (i == 0) {
                newEventParameter.structureArrayParam.setStructFirstParameter(true);
            }

            if (i == structParameterList.size() - 1) {
                newEventParameter.structureArrayParam.setStructLastParameter(true);
            }

            structureParameterList.add(newEventParameter);
        }
        return structureParameterList;
    }

    /**
     * This method creates a new parameter type object from a param XML element in the event schema file
     *
     * @param handler
     *            A schemahandler to look up parameter types
     * @param paramElement
     *            The param XML element
     * @return returns an EventParam object describing the parameter instance
     * @throws SchemaException
     * @throws DataConversionException
     */
    private static EventParameter getSingleParameter(final SchemaComponentHandler handler, final Element paramElement)
            throws SchemaException, DataConversionException {
        final String paramPreamble = handler.getSchema().getParamPreamble();

        String parameterName = paramElement.getText().trim().replaceFirst(paramPreamble, "");

        parameterName = addPrefixIfNameStartsWithDigit(parameterName);

        final String parameterType = getParameterTypeIfSpecified(paramElement, parameterName);

        final Parameter parameter = handler.getSchema().getParameterHandler().getMap().get(parameterType);

        if (parameter == null) {
            throw new SchemaException("Unknown parameter \"" + parameterType + "\" specified on event");
        }

        boolean useValidFlag = parameter.isUseValid();
        if (paramElement.getAttribute("usevalid") != null) {
            useValidFlag = paramElement.getAttribute("usevalid").getBooleanValue();
        }

        boolean optionalFlag = false;
        if (paramElement.getAttribute("optional") != null) {
            optionalFlag = paramElement.getAttribute("optional").getBooleanValue();
        }

        final EventParameter newEventParameter = new EventParameter(parameter);

        newEventParameter.name = parameterName;
        newEventParameter.useValid = useValidFlag;
        newEventParameter.optional = optionalFlag;
        newEventParameter.parameterDecoder = ParameterDecoderFactory.getParameterDecoder(newEventParameter);

        return newEventParameter;
    }

    private static String addPrefixIfNameStartsWithDigit(final String parameterName) {
        if (Character.isDigit(parameterName.charAt(0))) {
            return "P_" + parameterName;
        }
        return parameterName;
    }

    private static String getParameterTypeIfSpecified(final Element paramElement, final String parameterType) {
        if (paramElement.getAttribute("type") != null) {
            return paramElement.getAttribute("type").getValue().trim();
        }
        return parameterType;
    }

    /**
     * Get the name of the parameter
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the definition of this parameter
     *
     * @return the definition of the parameter
     */
    public Parameter getParameter() {
        return parameter;
    }

    /**
     * Return the Parser type for this parameter
     *
     * @return the Parser type
     */
    public ParserType getParserType() {
        return parserType;
    }

    /**
     * Return the type for this parameter
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Return the number of bytes used by this parameter
     *
     * @return the size (or minimum size if variable length) of this parameter in bytes
     */
    public int getNumberOfBytes() {
        return numberOfBytes;
    }

    /**
     * Return Start offset/Skip for Structured Attribute
     *
     * @return start skip
     */
    public int getStartSkip() {
        return structureArrayParam.getStartSkip();
    }

    /**
     * Return End offset/Skip for Structured Attribute
     *
     * @return end skip
     */
    public int getEndSkip() {
        return structureArrayParam.getEndSkip();
    }

    /**
     * Return Structure Size
     *
     * @return size
     */
    public int getStructSize() {
        return structureArrayParam.getStructSize();
    }

    /**
     * @param structSize
     *            - size
     */
    public void setStructSize(final int structSize) {
        structureArrayParam.setStructSize(structSize);
    }

    /**
     * Set Start offset/Skip for Structured Attribute
     *
     * @param startSkip
     *            - star skip
     */
    public void setStartSkip(final int startSkip) {
        structureArrayParam.setStartSkip(startSkip);
    }

    /**
     * Set End offset/Skip for Structured Attribute
     *
     * @param endSkip
     *            - end skip
     */
    public void setEndSkip(final int endSkip) {
        structureArrayParam.setEndSkip(endSkip);
    }

    /**
     * Set the number of bytes in this parameter, used on variable length parameters
     *
     * @param numberOfBytes
     *            - number of bytes
     */
    public void setNumberOfBytes(final int numberOfBytes) {
        this.numberOfBytes = numberOfBytes;
    }

    /**
     * Return the number of bits used by this parameter if it is bit packed
     *
     * @return the size (or minimum size if variable length) of this parameter packed in bits
     */
    public int getNumberOfBits() {
        return numberOfBits;
    }

    /**
     * Return if use of this parameter is valid
     *
     * @return true if the use of this parameter is valid
     */
    public boolean isUseValid() {
        return useValid;
    }

    /**
     * Return if this parameter is of variable length
     *
     * @return true if the parameter is variable length
     */
    public boolean isVariableLength() {
        return variableLength;
    }

    /**
     * Return if use of this parameter is optional
     *
     * @return true if the use of this parameter is optional
     */
    public boolean isOptional() {
        return optional;
    }

    /**
     * Return if this parameter is a structure array parameter
     *
     * @return true if the parameter is an array parameter
     */
    public boolean isStructArray() {
        return structureArrayParam.isStructArray();
    }

    /**
     * Return true if this parameter is the first parameter in a structure
     *
     * @return true if this parameter is the first parameter in a structure
     */
    public boolean isStructFirstParameter() {
        return structureArrayParam.isStructFirstParameter();
    }

    /**
     * Return true if this parameter is the last parameter in a structure
     *
     * @return true if this parameter is the last parameter in a structure
     */
    public boolean isStructLastParameter() {
        return structureArrayParam.isStructLastParameter();
    }

    /**
     * Return the maximum number of elements in the array if this parameter is a structure array parameter
     *
     * @return The maximum number of elements
     */
    public int getMaxStructArraySize() {
        return structureArrayParam.getMaxStructArraySize();
    }

    /**
     * Return the maximum number of valid elements in the array
     *
     * @return The maximum number of elements
     */
    public int getValidStructureArraySize() {
        return structureArrayParam.getValidStructureArraySize();
    }

    /**
     * Set Valid Structure Array Size
     *
     * @param validStructureArraySize
     *            - validStructureArraySize
     */
    public void setValidStructureArraySize(final int validStructureArraySize) {
        structureArrayParam.setValidStructureArraySize(validStructureArraySize);
    }

    /**
     * Set the name of the parameter, used to eliminate duplicate parameter names on an event and to prefix parameters with struct names
     *
     * @param name
     *            The parameter name
     */
    public void setName(final String name) {
        this.name = name;
    }

    public boolean isEventArrayParameter() {
        return eventArrayParameter;
    }

    public void setEventArrayParameter(final boolean isEventArrayParameter) {
        this.eventArrayParameter = isEventArrayParameter;
    }

    public boolean isValidLTEembeddedbitFlag() {
        return validLTEembeddedbitFlag;
    }

    public GenericParameterDecoder getParameterDecoder() {
        return parameterDecoder;
    }

    void setParameterDecoder(final GenericParameterDecoder parameterDecoder) {
        this.parameterDecoder = parameterDecoder;
    }

    public int getEventArrayParameterSize() {
        return eventArrayParameterSize;
    }

    public void setEventArrayParameterSize(final int eventArrayParameterSize) {
        this.eventArrayParameterSize = eventArrayParameterSize;
    }

    /**
     * Returns a string representation of this object
     */
    @Override
    public String toString() {
        return "EventParameter [name=" + name + ", parameter=" + parameter + ", type=" + type + ", parserType=" + parserType + ", numberOfBytes="
                + numberOfBytes + ", numberOfBits=" + numberOfBits + ", variableLength=" + variableLength + ", useValid=" + useValid + ", optional="
                + optional + ", validLTEembeddedbitFlag=" + validLTEembeddedbitFlag + ", structureArrayParam=" + structureArrayParam
                + ", GenericParameterDecoder=" + (parameterDecoder != null ? parameterDecoder.getClass().getCanonicalName() : "")
                + ", eventArrayParameter=" + eventArrayParameter + ", eventArrayParameterSize=" + eventArrayParameterSize + "]";
    }

    /**
     * Compare this parameter to another parameter, comparison based on name
     *
     * @param comparedEventParameter
     * @return the comparison value
     */
    @Override
    public int compareTo(final EventParameter comparedEventParameter) {
        return name.compareTo(comparedEventParameter.name);
    }

    /**
     * Compare two parameters, comparison based on name
     *
     * @param firstParameter
     *            the first parameter
     * @param secondParameter
     *            the second parameter
     * @return the comparison value
     */
    @Override
    public int compare(final EventParameter firstParameter, final EventParameter secondParameter) {
        return firstParameter.compareTo(secondParameter);
    }

    /**
     * Override the equals method for hash maps of event parameters
     */
    @Override
    public boolean equals(final Object other) {
        if (other instanceof EventParameter) {
            final EventParameter otherEventParameter = (EventParameter) other;
            return name.equals(otherEventParameter.getName());
        }
        return false;
    }

    /**
     * Override the hashCode hash maps of event parameters
     *
     * @return The hash code of the event parameter name
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

}

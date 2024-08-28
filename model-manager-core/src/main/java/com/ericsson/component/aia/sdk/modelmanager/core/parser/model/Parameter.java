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

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.jdom.Namespace;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.type.SchemaEnum;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParserType;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.TypeConversionParamToParserTypes;

/**
 * This class is used to hold a single Parameter
 */
public class Parameter implements Comparable<Parameter>, Comparator<Parameter> {

    private static final int NON_BIT_PACKED_VARIABLE_PARAMETER_LENGTH = -1;
    private static final String INTEGER_REGEX = "-?\\d+";

    private String name;

    private String type;

    private String description;

    private boolean bitPacked;

    private int numberOfBytes;

    private int numberOfBits;

    private boolean variableLength;

    private boolean useValid;

    private final long[] range = new long[2];

    private int resolution;

    private String unit;

    private String detailedDescription;

    private String comment;

    private Map<Integer, String> enumMap = null;

    private boolean validLTEembeddedbitFlag;

    /**
     * Instantiate a parameter as an object
     *
     * @param parameter
     *            the parameter element to read
     * @param namespace
     *            The parameter namespace
     * @param paramPreamble
     *            The preamble string on parameter names that is to be replaced
     * @param valuePreamble
     *            The preamble string on ENUM value names that is to be replaced
     * @param schemaType
     *            - schemaType
     * @throws SchemaException
     *             - exception
     */
    public Parameter(final Element parameter, final Namespace namespace, final String paramPreamble, final String valuePreamble,
                     final String schemaType)
            throws SchemaException {
        try {
            generateInformation(parameter, namespace, paramPreamble, valuePreamble);

            if (isCellTraceParameter(schemaType)) {
                processCellTraceEvent();
            }
        } catch (final Exception exception) {
            throw new SchemaException("Error parsing event schema element [" + parameter.getName() + "=" + name + "] ", exception);
        }
    }

    private void generateInformation(final Element parameter, final Namespace namespace, final String paramPreamble, final String valuePreamble)
            throws SchemaException {
        Element childElement;
        name = parameter.getChild("name", namespace).getText().trim().replaceFirst(paramPreamble, "");
        type = parameter.getChild("type", namespace).getText().trim();
        description = parameter.getChild("description", namespace).getText().trim().replaceAll("'", "");

        if (Character.isDigit(name.charAt(0))) {
            name = "P_" + name;
        }

        if (variableLengthType()) {
            setVariableLengthFields(parameter, namespace);
        } else {
            handleNonVariableLength(parameter, namespace);
        }

        checkRange(parameter, namespace);

        if ("ENUM".equals(type)) {
            toEnum(parameter.getChild("enumeration", namespace), valuePreamble);
        }

        instantiateOptionalElements(parameter, namespace);

        childElement = parameter.getChild("resolution", namespace);
        if (childElement != null) {
            resolution = Integer.valueOf(childElement.getText().trim());
        }

        childElement = parameter.getChild("unit", namespace);
        if (childElement != null) {
            unit = childElement.getText().trim();
        }

        childElement = parameter.getChild("comment", namespace);
        if (childElement != null) {
            comment = childElement.getText().trim().replaceAll("'", "");
        }

        childElement = parameter.getChild("detaileddescription", namespace);
        if (childElement != null) {
            comment = childElement.getText().trim().replaceAll("'", "");
        }
    }

    private void processCellTraceEvent() {
        validLTEembeddedbitFlag = false;
        if ("ENUM".equals(type) || "IBCD".equals(type) || "UINT".equals(type) || "LONG".equals(type) || "BOOLEAN".equals(type) || "FLOAT".equals(type)
                || "DOUBLE".equals(type)) {
            validLTEembeddedbitFlag = true;
        } else if (useValid) {
            numberOfBytes -= 1;
            numberOfBits = numberOfBytes * 8;
        }
    }

    private boolean isCellTraceParameter(final String schemaName) {
        return SchemaEnum.CELLTRACE.value().equals(schemaName);
    }

    /**
     * @param parameter
     * @param namespace
     */
    private void instantiateOptionalElements(final Element parameter, final Namespace namespace) {
        if (parameter.getChild("usevalid", namespace) != null) {
            final String useValidString = parameter.getChild("usevalid", namespace).getText().trim();
            useValid = ("yes".equalsIgnoreCase(useValidString) || "true".equalsIgnoreCase(useValidString));
        }
    }

    /**
     * @param parameter
     * @param namespace
     */
    private void checkRange(final Element parameter, final Namespace namespace) {
        if (parameter.getChild("range", namespace) != null) {
            range[0] = Long.valueOf(parameter.getChild("range", namespace).getChild("low", namespace).getText().trim());
            range[1] = Long.valueOf(parameter.getChild("range", namespace).getChild("high", namespace).getText().trim());
        } else if ("UINT".equals(type) || "LONG".equals(type)) {
            range[0] = 0;
            range[1] = (long) Math.pow(2, numberOfBits) - 1;
        }
    }

    /**
     * @param parameter
     * @param namespace
     */
    private void handleNonVariableLength(final Element parameter, final Namespace namespace) {
        if (notBitPacked(parameter, namespace)) {
            bitPacked = false;
            final String bytesText = parameter.getChild("numberofbytes", namespace).getText().trim();
            numberOfBytes = bytesText.matches(INTEGER_REGEX) ? Integer.valueOf(bytesText) : -1;
            if (numberOfBytes == -1) {
                variableLength = true;
                numberOfBits = -1;
            } else {
                numberOfBits = numberOfBytes * 8;
            }
        } else {
            bitPacked = true;
            numberOfBits = Integer.valueOf(parameter.getChild("numberofbits", namespace).getText().trim());
            if (numberOfBits == -1) {
                variableLength = true;
                numberOfBytes = -1;
            } else {
                numberOfBytes = (numberOfBits - 1) / 8 + 1;
            }
        }
    }

    /**
     * @param parameter
     * @param namespace
     * @return
     */
    private boolean notBitPacked(final Element parameter, final Namespace namespace) {
        return parameter.getChild("numberofbytes", namespace) != null;
    }

    /**
     * @param parameter
     * @param namespace
     */
    private void setVariableLengthFields(final Element parameter, final Namespace namespace) {
        variableLength = true;

        if (notBitPacked(parameter, namespace)) {
            bitPacked = false;
            numberOfBytes = NON_BIT_PACKED_VARIABLE_PARAMETER_LENGTH;
        } else {
            bitPacked = true;

            if (parameter.getChild("numberofbits", namespace) != null) {
                numberOfBits = Integer.valueOf(parameter.getChild("numberofbits", namespace).getText().trim());
            } else {
                numberOfBits = Integer.valueOf(parameter.getChild("lengthbits", namespace).getText().trim());
            }

            if (parameter.getChild("validlength", namespace) != null
                    && parameter.getChild("validlength", namespace).getChild("high", namespace) != null) {
                numberOfBytes = Integer.valueOf(parameter.getChild("validlength", namespace).getChild("high", namespace).getText().trim());
            } else {
                numberOfBytes = (numberOfBits - 1) / 8 + 1;
            }
        }
    }

    /**
     * @return
     */
    private boolean variableLengthType() {
        return "DNSNAME".equals(type) || "BYTEARRAY".equals(type);
    }

    /**
     * Set an MAP for this parameter
     *
     * @param enumParameter
     *            The ENUM XML element
     * @param valuePreamble
     *            The preamble string on ENUM value names that is to be replaced
     * @throws SchemaException
     */
    private void toEnum(final Element enumParameter, final String valuePreamble) throws SchemaException {
        enumMap = new HashMap<>();

        @SuppressWarnings("unchecked")
        final List<Element> enumValues = enumParameter.getChildren();

        for (final Element enumValue : enumValues) {
            final String internalName = enumValue.getAttributeValue("internal").replaceFirst(valuePreamble, "").trim();
            final int value = Integer.valueOf(enumValue.getAttributeValue("value").trim());

            enumMap.put(value, internalName);
        }
    }

    /**
     * Get the name of a parameter
     *
     * @return The parameter name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the type of a parameter
     *
     * @return The parameter type
     */
    public String getType() {
        return type;
    }

    /**
     * Get the Parser type of a parameter
     *
     * @return The Parser parameter type
     */
    public ParserType getParserType() {
        return TypeConversionParamToParserTypes.typeEventParameter2ParserTypes(type, numberOfBytes);
    }

    /**
     * Get the description of the parameter
     *
     * @return the parameter description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Determine if this parameter is bit packed
     *
     * @return true if the parameter is bit packed
     */
    public boolean isBitPacked() {
        return bitPacked;
    }

    /**
     * Get the number of bytes of the parameter The byte count of the unpacked parameter or minimum byte count for variable length parameters
     *
     * @return the number of bytes in the parameter
     */
    public int getNumberOfBytes() {
        return numberOfBytes;
    }

    /**
     * Get the number of bits of the parameter, the actual packed size of the parameter The bit count of the packed parameter or minimum bit count for
     * variable length parameters
     *
     * @return the number of bits in the parameter
     */
    public int getNumberOfBits() {
        return numberOfBits;
    }

    /**
     * Return if this parameter is of variable length
     *
     * @return true if this parameter is of variable length
     */
    public boolean isVariableLength() {
        return variableLength;
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
     * Get the range of values this parameter can have
     *
     * @return an array with two values for the low and high range values
     */
    public long[] getRange() {
        return range;
    }

    /**
     * Get the resolution of this parameter
     *
     * @return the resolution
     */
    public int getResolution() {
        return resolution;
    }

    /**
     * Get the units in which this parameter is expressed
     *
     * @return The units in which the parameter is expressed
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Get the detailed description of this parameter
     *
     * @return detailed description of the parameter
     */
    public String getDetailedDescription() {
        return detailedDescription;
    }

    /**
     * Get any comments made on this parameter
     *
     * @return comments on the parameter
     */
    public String getComment() {
        return comment;
    }

    /**
     * Get the ENUM values for a parameter
     *
     * @return the ENUM values
     */
    public Map<Integer, String> getEnumMap() {
        return enumMap;
    }

    /**
     * Method to dump the parameter to a string
     *
     * @return string containing the parameter
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        builder.append(name).append(",").append(bitPacked).append(",").append(numberOfBytes).append(",").append(numberOfBits).append(",")
                .append(variableLength).append(",").append(useValid).append(",").append(validLTEembeddedbitFlag).append(",").append(type.toString())
                .append(",[").append(range[0]).append("-").append(range[1]).append("]").append(",").append(resolution).append(",").append(unit);

        if ("ENUM".equals(type)) {
            builder.append("[");
            boolean firstLoop = true;
            for (final Integer enumValue : enumMap.keySet()) {
                builder.append(firstLoop ? "" : ",").append(enumValue).append(":").append(enumMap.get(enumValue));
                firstLoop = false;
            }
            builder.append("]");
        }
        builder.append(",").append(description).append(",").append(detailedDescription).append(",").append(comment);

        return builder.toString();
    }

    /**
     * Compare this parameter to another parameter, comparison based on name
     *
     * @param comparedParameter
     * @return the comparison value
     */
    @Override
    public int compareTo(final Parameter comparedParameter) {
        return name.compareTo(comparedParameter.name);
    }

    /**
     * Compare this parameter to another parameter, comparison based on name
     *
     * @param firstParameter
     * @param secondParameter
     * @return the comparison value
     */
    @Override
    public int compare(final Parameter firstParameter, final Parameter secondParameter) {
        return firstParameter.compareTo(secondParameter);
    }

    /**
     * Override the equals method for hash maps of parameters
     */
    @Override
    public boolean equals(final Object other) {
        if (other instanceof Parameter) {
            final Parameter otherParameter = (Parameter) other;
            return name.equals(otherParameter.getName());
        }
        return false;
    }

    /**
     * Override the hashCode hash maps of parameters
     *
     * @return The hash code of the parameter name
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * @return the embeddedbitFlag
     */
    public boolean isValidLTEembeddedbitFlag() {
        return validLTEembeddedbitFlag;
    }

}

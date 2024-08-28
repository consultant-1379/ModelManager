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

import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParserType.BOOLEAN;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParserType.BYTE;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParserType.BYTE_ARRAY;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParserType.CCSTRING;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParserType.DNSNAME;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParserType.DOUBLE;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParserType.FLOAT;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParserType.HEXSTRING;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParserType.IBCD;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParserType.INTEGER;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParserType.IPADDRESS;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParserType.IPADDRESSV6;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParserType.LONG;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParserType.OTHER;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParserType.SHORT;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParserType.SHORT_ARRAY;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParserType.STRING;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParserType.TBCD;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;

/**
 * This class handles conversion to Parser's Types
 */
public class TypeConversionParamToParserTypes {

    static {
        final Map<String, ParserType> typeMap = new HashMap<String, ParserType>();
        typeMap.put("BIGINT", LONG);
        typeMap.put("BINARY", BYTE_ARRAY);
        typeMap.put("BYTEARRAY", BYTE_ARRAY);
        typeMap.put("FROREF", BYTE_ARRAY);
        typeMap.put("BOOLEAN", BOOLEAN);
        typeMap.put("DNSNAME", DNSNAME);
        typeMap.put("STRING", STRING);
        typeMap.put("CCSTRING", CCSTRING);
        typeMap.put("HEXSTRING", HEXSTRING);
        typeMap.put("IPADDRESS", IPADDRESS);
        typeMap.put("IPADDRESSV6", IPADDRESSV6);
        typeMap.put("TBCD", TBCD);
        typeMap.put("DOUBLE", DOUBLE);
        typeMap.put("FLOAT", FLOAT);
        typeMap.put("TIMESTAMP", LONG);
        typeMap.put("SHORTARRAY", SHORT_ARRAY);
        typeMap.put("SHORT", SHORT);
        MAP_PARAM_TO_JAVA_TYPES = Collections.unmodifiableMap(typeMap);

    }

    public static final String P_IBCD = "IBCD";

    private static final String LONG_STRING_VALUE = "LONG";

    private static final String UINT = "UINT";

    private static final String ENUM = "ENUM";

    private static final Map<String, ParserType> MAP_PARAM_TO_JAVA_TYPES;

    /**
     * Default
     */
    private TypeConversionParamToParserTypes() {

    }

    /**
     * Convert an Event Parameter type to Parser's Types
     *
     * @param parameterType
     *            type
     * @param numberOfBytes
     *            - number of bytes
     * @return The parameter type as Parser's Types
     * @throws SchemaException
     *             - exception
     */
    public static ParserType typeEventParameter2ParserTypes(final String parameterType, final int numberOfBytes) {

        if (P_IBCD.equals(parameterType)) {
            return IBCD;
        }

        if (ENUM.equals(parameterType) || UINT.equals(parameterType) || LONG_STRING_VALUE.equals(parameterType)) {
            return numericTypeConversions(numberOfBytes);
        }

        return getFromMap(parameterType);
    }

    /**
     * @param parameterType
     *            type
     * @return parser type
     */
    private static ParserType getFromMap(final String parameterType) {
        final ParserType returnType = MAP_PARAM_TO_JAVA_TYPES.get(parameterType);
        return (returnType != null) ? returnType : OTHER;
    }

    /**
     * @param numberOfBytes
     *            - number of bytes
     * @return - parser type
     */
    private static ParserType numericTypeConversions(final int numberOfBytes) {
        if (isByte(numberOfBytes)) {
            return BYTE;
        } else if (isUnassignedInteger(numberOfBytes)) {
            return INTEGER;
        } else if (isLong(numberOfBytes)) {
            return LONG;
        }
        return OTHER;
    }

    /**
     * @param numberOfBytes
     *            - number of bytes
     * @return type
     */
    private static boolean isLong(final int numberOfBytes) {
        return numberOfBytes >= 3 & numberOfBytes <= 8;
    }

    /**
     * @param numberOfBytes
     *            - number of bytes
     * @return if is unsigned
     */
    private static boolean isUnassignedInteger(final int numberOfBytes) {
        return numberOfBytes == 2;
    }

    /**
     * @param numberOfBytes
     *            - number of bytes
     * @return if is byte
     */
    private static boolean isByte(final int numberOfBytes) {
        return numberOfBytes >= 0 & numberOfBytes <= 1;
    }
}
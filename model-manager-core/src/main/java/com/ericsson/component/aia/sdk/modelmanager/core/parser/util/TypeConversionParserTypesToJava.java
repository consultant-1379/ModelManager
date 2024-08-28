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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles conversion from Parser Types to Java type
 */
public class TypeConversionParserTypesToJava {
    private static final Map<ParserType, String> MAPSQLTOJAVA;
    static {
        final Map<ParserType, String> typeMap = new HashMap<ParserType, String>();
        typeMap.put(ParserType.LONG, "long");
        typeMap.put(ParserType.BYTE_ARRAY, "byte[]");
        typeMap.put(ParserType.BOOLEAN, "boolean");
        typeMap.put(ParserType.STRING, "String");
        typeMap.put(ParserType.DOUBLE, "double");
        typeMap.put(ParserType.FLOAT, "float");
        typeMap.put(ParserType.INTEGER, "int");
        typeMap.put(ParserType.SHORT, "short");
        typeMap.put(ParserType.LONG, "long");
        typeMap.put(ParserType.BYTE, "byte");
        typeMap.put(ParserType.SHORT, "short");
        typeMap.put(ParserType.SHORT_ARRAY, "short[]");
        typeMap.put(ParserType.IBCD, "String");
        typeMap.put(ParserType.TBCD, "String");
        typeMap.put(ParserType.IPADDRESS, "String");
        typeMap.put(ParserType.IPADDRESSV6, "String");
        typeMap.put(ParserType.DNSNAME, "String");
        typeMap.put(ParserType.HEXSTRING, "String");
        typeMap.put(ParserType.CCSTRING, "String");
        MAPSQLTOJAVA = Collections.unmodifiableMap(typeMap);

    }

    /**
     * Private constructor
     */
    private TypeConversionParserTypesToJava() {

    }

    /**
     * Method to return the java type of a Parser's type; this method only converts types used in xStream
     *
     * @param sqlType
     *            - type
     * @return value
     */
    public static String typeSqlToJava(final ParserType sqlType) {
        final String returnType = MAPSQLTOJAVA.get(sqlType);
        return (returnType != null) ? returnType : "null";
    }
}
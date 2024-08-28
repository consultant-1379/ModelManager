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
package com.ericsson.component.aia.sdk.modelmanager.core.parser.avro;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.ParserType;

/**
 * This class handles conversion from Parser Types to Java type
 */
public class TypeConversionParserTypesToJson {
    private static final Map<ParserType, String> MAPSQLTOJAVA;
    static {
        final Map<ParserType, String> typeMap = new HashMap<ParserType, String>();
        typeMap.put(ParserType.LONG, "long");
        typeMap.put(ParserType.BYTE_ARRAY, "bytes");
        typeMap.put(ParserType.BOOLEAN, "boolean");
        typeMap.put(ParserType.STRING, "string");
        typeMap.put(ParserType.DOUBLE, "double");
        typeMap.put(ParserType.FLOAT, "float");
        typeMap.put(ParserType.INTEGER, "int");
        typeMap.put(ParserType.SHORT, "short");
        typeMap.put(ParserType.LONG, "long");
        typeMap.put(ParserType.BYTE, "int");
        typeMap.put(ParserType.SHORT, "short");
        typeMap.put(ParserType.SHORT_ARRAY, "bytes");
        typeMap.put(ParserType.IBCD, "string");
        typeMap.put(ParserType.TBCD, "string");
        typeMap.put(ParserType.IPADDRESS, "string");
        typeMap.put(ParserType.IPADDRESSV6, "string");
        typeMap.put(ParserType.DNSNAME, "string");
        typeMap.put(ParserType.HEXSTRING, "string");
        typeMap.put(ParserType.CCSTRING, "string");
        MAPSQLTOJAVA = Collections.unmodifiableMap(typeMap);

    }

    private TypeConversionParserTypesToJson() {

    }

    /**
     * Method to return the java type of a Parser's type; this method only converts types used in xStream
     *
     * @param sqlType
     *            - sql type
     * @return type sql
     */
    public static String typeSqlToJava(final ParserType sqlType) {
        final String returnType = MAPSQLTOJAVA.get(sqlType);
        return (returnType != null) ? returnType : "null";
    }
}
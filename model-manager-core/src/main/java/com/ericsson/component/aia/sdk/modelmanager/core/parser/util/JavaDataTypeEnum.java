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

/**
 * @author aia
 */
public enum JavaDataTypeEnum {

    STRING(ParserType.STRING,
            "getString",
            "DEFAULT_STRING_VALUE"),
    BYTE(ParserType.BYTE,
            "getByte",
            "DEFAULT_BYTE_VALUE"),
    SHORT(ParserType.SHORT,
            "getShort",
            "DEFAULT_SHORT_VALUE"),
    INTEGER(ParserType.INTEGER,
            "getUnsignedIntegerAsInteger",
            "DEFAULT_INT_VALUE"),
    LONG(ParserType.LONG,
            "getLong",
            "DEFAULT_LONG_VALUE"),
    BYTE_ARRAY(ParserType.BYTE_ARRAY,
            "getByteArrayByteArray",
            "DEFAULT_BYTE_ARRAY_VALUE"),
    BOOLEAN(ParserType.BOOLEAN,
            "getByteArrayBoolean",
            "DEFAULT_BOOLEAN_VALUE"),
    FLOAT(ParserType.FLOAT,
            "getByteArrayFloat",
            "DEFAULT_FLOAT_VALUE"),
    DOUBLE(ParserType.DOUBLE,
            "getByteArrayDouble",
            "DEFAULT_DOUBLE_VALUE"),
    SHORT_ARRAY(ParserType.SHORT_ARRAY,
            "getShortArray",
            "DEFAULT_SHORT_ARRAY_VALUE"),
    IBCD(ParserType.IBCD,
            "getByteArrayIBCD",
            "DEFAULT_STRING_VALUE"),
    TBCD(ParserType.TBCD,
            "getByteArrayTBCDString",
            "DEFAULT_STRING_VALUE"),
    IPADDRESS(ParserType.IPADDRESS,
            "getByteArrayDottedDecimalString",
            "DEFAULT_STRING_VALUE"),
    IPADDRESSV6(ParserType.IPADDRESSV6,
            "getByteArrayIPV6String",
            "DEFAULT_STRING_VALUE"),
    DNSNAME(ParserType.DNSNAME,
            "getByteArray3GPPString",
            "DEFAULT_STRING_VALUE"),
    HEXSTRING(ParserType.HEXSTRING,
            "getByteArrayHexString",
            "DEFAULT_STRING_VALUE"),
    CCSTRING(ParserType.CCSTRING,
            "getByteArrayCCString",
            "DEFAULT_STRING_VALUE");

    private final ParserType type;

    private final String value;

    private final String initVale;

    /**
     * Default constructor
     *
     * @param type
     *            - type
     * @param value
     *            - value
     * @param initVale
     *            - init value
     */
    JavaDataTypeEnum(final ParserType type, final String value, final String initVale) {
        this.type = type;
        this.value = value;
        this.initVale = initVale;
    }

    /**
     *
     * @return type
     */
    public ParserType getType() {
        return type;
    }

    /**
     *
     * @return type
     */
    public String getValue() {
        return value;
    }

    /**
     *
     * @param type
     *            - type
     * @return value
     */
    public static String getValue(final ParserType type) {
        for (final JavaDataTypeEnum schema : JavaDataTypeEnum.values()) {
            if (schema.type == type) {
                return schema.getValue();
            }
        }
        return null;
    }

    /**
     *
     * @param type
     *            - type
     * @return value
     */
    public static String getInitValue(final ParserType type) {
        for (final JavaDataTypeEnum schema : JavaDataTypeEnum.values()) {
            if (schema.type == type) {
                return schema.getInitVale();
            }
        }
        return null;
    }

    /**
     *
     * @return value
     */
    public String getInitVale() {
        return initVale;
    }
}

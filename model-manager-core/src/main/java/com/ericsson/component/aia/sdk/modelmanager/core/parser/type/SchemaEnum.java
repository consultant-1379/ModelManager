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
package com.ericsson.component.aia.sdk.modelmanager.core.parser.type;

/**
 * @author aia
 */
public enum SchemaEnum {

    CELLTRACE("celltrace"), EBM("ebm"), CTUM("ctum"), CORRELATION("correlation");
    private final String value;

    /**
     * Default.
     *
     * @param schema
     *            - scherma
     */
    SchemaEnum(final String schema) {
        value = schema.toLowerCase();
    }

    /**
     * @return string
     */
    public String value() {
        return value;
    }

    /**
     * @param schemaName
     *            - schema
     * @return schema
     */
    public static SchemaEnum fromValue(final String schemaName) {
        final String lowerCaseSchemaName = schemaName.toLowerCase();
        for (final SchemaEnum schema : SchemaEnum.values()) {
            if (schema.value.equals(lowerCaseSchemaName)) {
                return schema;
            }
        }
        throw new IllegalArgumentException(schemaName);
    }

}

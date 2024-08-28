/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.sdk.modelmanager.exceptions;

import com.ericsson.component.aia.sdk.templatemanager.exception.code.ExceptionCode;

/**
 * @author ezsalro
 *
 */
public enum ModelManagerExceptionCodes implements ExceptionCode {

    NOT_AUTHORIZED("4000001"),
    ERROR_REGISTERING_SERVICES("4000002"),
    MODEL_ALREADY_EXISTS("4000003"),
    ERROR_INVOKING_METADATASERVICE_ON_SAVE("4000004"),
    ERROR_INVOKING_METADATASERVICE_ON_LIST("4000005"),
    ERROR_INVOKING_METADATASERVICE_ON_GET("4000006"),
    ERROR_INVOKING_METADATASERVICE_ON_DELETE("4000007"),
    MODEL_NOT_FOUND("4000008"),
    ERROR_INVOKING_METADATASERVICE_ON_UPDATE("4000009");

    private final String code;

    /**
     * @param code
     *            error code
     */
    ModelManagerExceptionCodes(final String code) {
        this.code = code;
    }

    /**
     * @param text
     *            code in string format
     * @return the equivalent enum
     */
    public ModelManagerExceptionCodes getExceptionCodes(final String text) {
        for (final ModelManagerExceptionCodes code : ModelManagerExceptionCodes.values()) {
            if (code.getCode().equals(text)) {
                return code;
            }
        }
        return null;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return this.code;
    }

}

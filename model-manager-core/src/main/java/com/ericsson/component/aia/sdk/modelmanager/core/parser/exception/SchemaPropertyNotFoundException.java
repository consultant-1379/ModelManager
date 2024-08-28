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
package com.ericsson.component.aia.sdk.modelmanager.core.parser.exception;

/**
 * @author aia
 */
public class SchemaPropertyNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -3213062064845240453L;

    /**
     * @param message
     *            - message
     */
    public SchemaPropertyNotFoundException(final String message) {
        super(message);
    }

    /**
     * @param message
     *            - message
     * @param exception
     *            - exception
     */
    public SchemaPropertyNotFoundException(final String message, final Exception exception) {
        super(message, exception);
    }
}

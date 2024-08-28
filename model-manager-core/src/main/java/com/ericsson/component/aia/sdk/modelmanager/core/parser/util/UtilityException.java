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
 * Enclosing class for utility exceptions
 *
 *
 */
public class UtilityException extends Exception {
    private static final long serialVersionUID = -6052785998652498220L;

    /**
     * Default Constructor.
     */
    public UtilityException() {
        super();
    }

    /**
     * @param message
     *            - error message
     * @param cause
     *            - the error cause
     */
    public UtilityException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     *            - error message
     */
    public UtilityException(final String message) {
        super(message);
    }

    /**
     * @param cause
     *            - the error cause
     */
    public UtilityException(final Throwable cause) {
        super(cause);
    }

}

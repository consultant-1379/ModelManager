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
public class InvalidTimeException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * @param message
     *            - message
     */
    public InvalidTimeException(final String message) {
        super(message);
    }

}

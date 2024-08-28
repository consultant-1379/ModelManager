/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.sdk.modelmanager.core.parser.exception;

import java.io.FileNotFoundException;

/**
 * @author aia
 */
public class ResourceNotFoundException extends FileNotFoundException {

    private static final long serialVersionUID = -2493787465601743008L;

    /**
     * @param message
     *            - message
     */
    public ResourceNotFoundException(final String message) {
        super(message);
    }

}

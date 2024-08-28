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
package com.ericsson.component.aia.sdk.modelmanager.core.exception;

import com.ericsson.component.aia.sdk.modelmanager.exceptions.ModelManagerException;
import com.ericsson.component.aia.sdk.templatemanager.exception.code.ExceptionCode;

/**
 * @author ezsalro
 */
public class ParserAlreadyExistsException extends ModelManagerException {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor
     */
    public ParserAlreadyExistsException() {
        super();
    }

    /**
     * @param code
     *            - code
     * @param msg
     *            - msg
     * @param exception
     *            - exception
     */
    public ParserAlreadyExistsException(final ExceptionCode code, final String msg, final Throwable exception) {
        super(code, msg, exception);
    }

    /**
     * @param code
     *            - code
     * @param msg
     *            - msg
     */
    public ParserAlreadyExistsException(final ExceptionCode code, final String msg) {
        super(code, msg);
    }

    /**
     * @param code
     *            - code
     * @param exception
     *            - exception
     */
    public ParserAlreadyExistsException(final ExceptionCode code, final Throwable exception) {
        super(code, exception);
    }

    /**
     * @param code
     *            - code
     * @param msg
     *            - msg
     * @param runtimeException
     *            - exception
     */
    public ParserAlreadyExistsException(final String code, final String msg, final String runtimeException) {
        super(code, msg, runtimeException);
    }

    /**
     * @param code
     *            - code
     * @param msg
     *            - msg
     * @param exception
     *            - exception
     * @param runtimeException
     *            - excpetion
     */
    public ParserAlreadyExistsException(final String code, final String msg, final Throwable exception, final String runtimeException) {
        super(code, msg, exception, runtimeException);
    }

    /**
     * @param code
     *            - code
     * @param msg
     *            - msg
     * @param exception
     *            - exception
     */
    public ParserAlreadyExistsException(final String code, final String msg, final Throwable exception) {
        super(code, msg, exception);
    }

}

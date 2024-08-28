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
package com.ericsson.component.aia.sdk.modelmanager.core.parser.resource;

import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.ResourceNotFoundException;

/**
 * @author aia
 */
public interface ResourceProvider {

    Logger LOGGER = LoggerFactory.getLogger(ResourceProvider.class);

    /**
     * @param fileName
     *            - file name
     * @return InputStream
     * @throws ResourceNotFoundException
     *             - exception
     */
    InputStream getFileResourceAsStream(final String fileName) throws ResourceNotFoundException;

    /**
     * @param fileName
     *            - file name
     * @return URL
     * @throws ResourceNotFoundException
     *             - exception
     */
    URL getFileResourceURL(final String fileName) throws ResourceNotFoundException;

    /**
     * @param fileName
     *            - file
     * @return file
     * @throws ResourceNotFoundException
     *             - exception
     */
    String getFileResourcePath(final String fileName) throws ResourceNotFoundException;

}

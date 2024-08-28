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
package com.ericsson.component.aia.sdk.modelmanager.core.parser.resource;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.ResourceNotFoundException;

/**
 * @author aia
 */
public class ResourceFileFinder implements ResourceProvider {

    private final static List<ResourceProvider> CLASSES = new ArrayList<>();

    {
        CLASSES.add(new ReadResourceFileFromClassPath());
        CLASSES.add(new ReadResourceFileFromFilePath());
    }

    @Override
    public InputStream getFileResourceAsStream(final String fileName) throws ResourceNotFoundException {
        for (final ResourceProvider readResource : CLASSES) {
            final InputStream resourceFileInputStream = readResource.getFileResourceAsStream(fileName);

            if (resourceFileInputStream != null) {
                return resourceFileInputStream;
            }
        }
        throw new ResourceNotFoundException("cound not get file resource: " + fileName);

    }

    @Override
    public URL getFileResourceURL(final String fileName) throws ResourceNotFoundException {
        for (final ResourceProvider readResource : CLASSES) {
            final URL resourceFileURL = readResource.getFileResourceURL(fileName);
            if (resourceFileURL != null) {
                return resourceFileURL;
            }
        }
        throw new ResourceNotFoundException("cound not get file resource: " + fileName);
    }

    @Override
    public String getFileResourcePath(final String fileName) throws ResourceNotFoundException {
        for (final ResourceProvider readResource : CLASSES) {
            final String resourceFile = readResource.getFileResourcePath(fileName);
            if (resourceFile != null) {
                return resourceFile;
            }
        }
        throw new ResourceNotFoundException("cound not get file resource: " + fileName);
    }

}

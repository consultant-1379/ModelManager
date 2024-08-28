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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.ResourceNotFoundException;

/**
 * @author aia
 */
public class ReadResourceFileFromFilePath implements ResourceProvider {
    @Override
    public InputStream getFileResourceAsStream(final String resourceName) throws ResourceNotFoundException {
        InputStream inputStream = findFileWithPath(resourceName);
        if (inputStream != null) {
            LOGGER.trace("on file path, for {} found {}", resourceName, inputStream);
            return inputStream;
        }

        final String lastResourceNameInPath = Paths.get(resourceName).getFileName().toString();
        LOGGER.trace("looking for resource {} without full path ", lastResourceNameInPath);
        inputStream = findFileWithPath(lastResourceNameInPath);

        if (inputStream != null) {
            LOGGER.trace("found resource {} at {}", lastResourceNameInPath, inputStream);
            return inputStream;
        }

        LOGGER.warn("unable to find resource {} returning null", lastResourceNameInPath);
        return inputStream;
    }

    private InputStream findFileWithPath(final String resourceName) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(resourceName);
        } catch (final IOException ioException) {
            LOGGER.error("Could not load schema from filepath: {}", resourceName, ioException);
        }
        return inputStream;
    }

    @Override
    public URL getFileResourceURL(final String resourceName) throws ResourceNotFoundException {
        URL fileURL = findFileAsURL(resourceName);
        if (fileURL != null) {
            LOGGER.trace("on file path, for {} found {}", resourceName, fileURL);
            return fileURL;
        }
        final String lastResourceNameInPath = Paths.get(resourceName).getFileName().toString();
        LOGGER.trace("looking for resource {} without full path {} ", lastResourceNameInPath);

        fileURL = findFileAsURL(lastResourceNameInPath);
        if (fileURL != null) {
            LOGGER.trace("found resource {} at {}", lastResourceNameInPath, fileURL);
            return fileURL;
        }

        LOGGER.warn("unable to find resource {} returning null", lastResourceNameInPath);
        return fileURL;
    }

    private URL findFileAsURL(final String resourceName) {
        final Path path = Paths.get(resourceName);

        final URL fileURL = null;
        try {
            if (Files.exists(path)) {
                return path.toUri().toURL();
            }
        } catch (final MalformedURLException malformedURLException) {
            LOGGER.error("Could not create URL from filepath: {}", resourceName, malformedURLException);
        }
        return fileURL;
    }

    @Override
    public String getFileResourcePath(final String resourceName) throws ResourceNotFoundException {
        final URL fileResourceURL = getFileResourceURL(resourceName);
        return fileResourceURL != null ? fileResourceURL.toString() : null;
    }
}
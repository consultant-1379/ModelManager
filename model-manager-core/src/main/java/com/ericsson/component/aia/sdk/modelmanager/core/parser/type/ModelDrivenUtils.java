/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.sdk.modelmanager.core.parser.type;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaPropertyNotFoundException;

/**
 * @author aia
 */
public class ModelDrivenUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelDrivenUtils.class);

    private ModelDrivenUtils() {

    }

    /**
     * @param directoryPath
     *            - directory
     * @return the files list
     */
    public static List<String> getFiles(final Path directoryPath) {
        final List<String> files = new ArrayList<>();
        try {
            Files.walkFileTree(directoryPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                    files.add(file.toString());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(final Path file, final IOException exc) throws IOException {
                    LOGGER.error("Unable to process {}", file, exc);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (final IOException ioException) {
            LOGGER.error("Unexpected error occured while processing dir {}", directoryPath, ioException);
        }

        return files;
    }

    /**
     * @param propertyName
     *            - property
     * @return the long property
     */
    public static long getLongProperty(final String propertyName) {
        final String propertyVal = getProperty(propertyName);
        try {
            return Long.parseLong(propertyVal);
        } catch (final NumberFormatException numberFormatException) {
            LOGGER.error("Unable to find {} property ", propertyName);
            throw new SchemaPropertyNotFoundException("Missing mandatory property " + propertyName, numberFormatException);
        }

    }

    /**
     * @param propertyName
     *            - property
     * @return the property
     */
    public static String getProperty(final String propertyName) {
        final String absoluteFileName = System.getProperty(propertyName);
        if (absoluteFileName == null || absoluteFileName.isEmpty()) {
            LOGGER.error("Unable to find {} property ", propertyName);
            throw new SchemaPropertyNotFoundException("Missing mandatory property " + propertyName);
        }
        return absoluteFileName;
    }
}

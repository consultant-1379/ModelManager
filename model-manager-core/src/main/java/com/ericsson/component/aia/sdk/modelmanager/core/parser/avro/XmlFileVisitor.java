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
package com.ericsson.component.aia.sdk.modelmanager.core.parser.avro;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link FileVisitor} that stores any xml files found in the specified directory into a list and exposes that list through a
 * getter.
 */
public class XmlFileVisitor implements FileVisitor<Path> {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlFileVisitor.class);
    private static final String XML_FILE_EXTENSION = ".xml";
    private final List<Path> xmlFilePaths = new ArrayList<>();

    @Override
    public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
        LOGGER.info("Processing directory: [{}]", dir.toString());
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        if (file.toString().endsWith(XML_FILE_EXTENSION)) {
            LOGGER.info("Processing file: [{}]", file.toString());
            xmlFilePaths.add(file);
        } else {
            LOGGER.info("Skipping file: [{}]", file.toString());
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(final Path file, final IOException exc) throws IOException {
        LOGGER.error("Failed to process file: [{}]", file.toString(), exc);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
        LOGGER.info("Finished processing directory: [{}]", dir.toString());
        return FileVisitResult.CONTINUE;
    }

    /**
     * @return a list of xml filepaths.
     */
    public List<Path> getXmlFilePaths() {
        return xmlFilePaths;
    }
}

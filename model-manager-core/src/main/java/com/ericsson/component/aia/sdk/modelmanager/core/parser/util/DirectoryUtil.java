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

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aia
 */
public class DirectoryUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryUtil.class);

    private File resourceDirectory;

    private File generatedDirectory;

    /**
     * @param resourceDirectory
     *            - resourceDirectory
     * @param generatedDirectory
     *            - generatedDirectory
     */
    public DirectoryUtil(final File resourceDirectory, final File generatedDirectory) {
        this.resourceDirectory = resourceDirectory;
        this.generatedDirectory = generatedDirectory;

    }

    /**
     * Default.
     */
    public DirectoryUtil() {
    }

    /**
     * @param file
     *            - file
     * @throws IOException
     *             - exception
     */
    public void createDirectoryIfNotExists(final File file) throws IOException {
        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new IOException("\"" + file.getAbsolutePath() + "\" is not a directory");
            }
        } else {
            if (!file.mkdirs()) {
                throw new IOException("\"" + file.getName() + "\" could not be created");
            }
        }
    }

    /**
     * @throws IOException
     *             - exception
     */
    public void initializeDirectoryStructure() throws IOException {
        if (!resourceDirectory.isDirectory()) {
            throw new IOException("\"" + resourceDirectory.getAbsolutePath() + "\" is not a directory");
        }
        createDirectoryIfNotExists(generatedDirectory);
    }

    /**
     * @param folder
     *            - folder
     * @param foldername
     *            - folder name
     * @return newly created folder
     */
    public File createFolder(final File folder, final String foldername) {
        try {
            final File childFolder = new File(folder, foldername);
            childFolder.mkdir();
            return childFolder;
        } catch (final Exception e) {
            LOGGER.error("Unable to create folder:{}", foldername, e);
            return null;
        }
    }
}

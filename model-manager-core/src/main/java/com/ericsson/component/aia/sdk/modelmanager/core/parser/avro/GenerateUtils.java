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
package com.ericsson.component.aia.sdk.modelmanager.core.parser.avro;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * This class contains static methods to help the generation process
 */
public class GenerateUtils {

    private GenerateUtils() {

    }

    /**
     * This method adds the given directory to the class path
     *
     * @param directory
     *            The directory to add
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws MalformedURLException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void addDirectoryToClasspath(final File directory) {
        Method method;
        try {
            method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
            method.setAccessible(true);
            method.invoke(ClassLoader.getSystemClassLoader(), new Object[] { directory.toURI().toURL() });
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | MalformedURLException e) {
            e.printStackTrace();
            throw new GenerationException(e);
        }
    }

    /**
     * This method checks if a generate is required by checking if any files in the target are older than any of the files in the files in the source
     *
     * @param source
     *            The source file or directory structure
     * @param target
     *            The target file or directory structure
     * @return true if a generation is needed
     */
    protected static boolean generateRequired(final File source, final File target) {
        return getMostRecentTimestamp(source) > getLeastRecentTimestamp(target);
    }

    /**
     * Get the time stamp of the most recent file update in the source hierarchy
     *
     * @param source
     *            The source hierarchy
     * @return The time stamp of the newest file update
     */
    private static long getMostRecentTimestamp(final File source) {

        if (source.isFile()) {
            return source.lastModified();
        }

        long mostRecentTimestamp = 0;

        for (final File sourceFile : source.listFiles()) {
            long currentMostRecentTimestamp = 0;
            if (sourceFile.isDirectory()) {
                currentMostRecentTimestamp = getMostRecentTimestamp(sourceFile);
            } else {
                currentMostRecentTimestamp = sourceFile.lastModified();
            }

            if (currentMostRecentTimestamp > mostRecentTimestamp) {
                mostRecentTimestamp = currentMostRecentTimestamp;
            }
        }

        return mostRecentTimestamp;
    }

    /**
     * Get the time stamp of the least recent file update in the target hierarchy
     *
     * @param target
     *            The target hierarchy
     * @return The time stamp of the oldest file update
     */
    private static long getLeastRecentTimestamp(final File target) {

        if (target.isFile()) {
            return target.lastModified();
        }

        if (!target.isDirectory() || target.listFiles().length == 0) {
            return 0;
        }

        long leastRecentTimestamp = 0;

        for (final File targetFile : target.listFiles()) {
            long currentLeastRecentTimestamp = 0;

            if (targetFile.isDirectory()) {
                currentLeastRecentTimestamp = getLeastRecentTimestamp(targetFile);
            } else {
                currentLeastRecentTimestamp = targetFile.lastModified();
            }

            if (leastRecentTimestamp <= 0 || currentLeastRecentTimestamp < leastRecentTimestamp) {
                leastRecentTimestamp = currentLeastRecentTimestamp;
            }
        }

        return leastRecentTimestamp;
    }
}

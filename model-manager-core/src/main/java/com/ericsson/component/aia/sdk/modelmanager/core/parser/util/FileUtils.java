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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides static utility methods for searching directories, and for getting information from file names
 *
 */
public class FileUtils {

    public static final String UNKNOWN_NE_NAME = "unknown";

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    private static final String MANAGED_ELEMENT_NE_NAME = "ManagedElement=";
    private static final int MANAGED_ELEMENT_NE_NAME_LENGTH = MANAGED_ELEMENT_NE_NAME.length();
    private static final String MECONTEXT_NE_NAME = "MeContext=";
    private static final int MECONTEXT_NE_NAME_LENGTH = MECONTEXT_NE_NAME.length();
    private static final String SUBNETWORK_SUBSET_NAME = "SubNetwork=";
    private static final int SUBNETWORK_SUBSET_NAME_LENGTH = SUBNETWORK_SUBSET_NAME.length();
    private static final int SHORT_DATE_UNDERSCORE_POS = 19;

    private static final int LONG_DATE_LENGTH = 18; // YYYYMMDD.HHMM+HHMM
    private static final int SHORT_DATE_LENGTH = 13; // YYYYMMDD.HHMM

    /**
     * Default constructor
     */
    private FileUtils() {

    }

    /**
     * Method to get the NE name from a path string
     *
     * @param pathString
     *            The string to search for a NE name
     * @return The NE name
     */
    public static String pathToNEName(final String pathString) {
        int startIndex = pathString.indexOf(MANAGED_ELEMENT_NE_NAME);

        if (startIndex == -1) {
            startIndex = pathString.indexOf(MECONTEXT_NE_NAME);
            if (startIndex == -1) {
                final String[] directories = pathString.split("%~%");
                if (directories != null && directories.length >= 2) {
                    return directories[directories.length - 2];
                }
                return UNKNOWN_NE_NAME;
            }
            startIndex += MECONTEXT_NE_NAME_LENGTH;
        } else {
            startIndex += MANAGED_ELEMENT_NE_NAME_LENGTH;
        }

        int endIndex = pathString.indexOf(File.separator, startIndex);

        if (endIndex == -1) {
            endIndex = pathString.indexOf("%~%", startIndex);
            if (endIndex == -1) {
                return UNKNOWN_NE_NAME;
            }
        }
        return pathString.substring(startIndex, endIndex);
    }

    /**
     * Method to get the SubNetwork name from a path string
     *
     * @param pathString
     *            - The string to search for a NE name
     * @return The NE name
     */
    public static String pathToSubNetName(final String pathString) {
        int startIndex = pathString.lastIndexOf(SUBNETWORK_SUBSET_NAME);

        if (startIndex == -1) {
            return UNKNOWN_NE_NAME;
        }
        startIndex += SUBNETWORK_SUBSET_NAME_LENGTH;

        int endIndex = pathString.indexOf(",MeContext=", startIndex);

        if (endIndex == -1) {
            endIndex = pathString.indexOf("%~%", startIndex);
            if (endIndex == -1) {
                return UNKNOWN_NE_NAME;
            }
        }
        return pathString.substring(startIndex, endIndex);
    }

    /**
     * Method to get a calendar time from a file name
     *
     * @param fileNamePath
     *            -The file name
     * @return calendar
     * @throws ParseException
     *             - exception
     */
    public static Calendar nameToCalendar(final String fileNamePath) throws ParseException {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        final DateFormat longDateFormat = new SimpleDateFormat("yyyyMMdd.HHmmZ");
        final DateFormat shortDateFormat = new SimpleDateFormat("yyyyMMdd.HHmm");

        final String fileName = getFromDatePart(fileNamePath);

        final int underscoreLocation = fileName.indexOf('_');

        try {
            if (underscoreLocation > SHORT_DATE_UNDERSCORE_POS) {
                calendar.setTime(longDateFormat.parse(fileName.substring(0, LONG_DATE_LENGTH)));
            } else {
                calendar.setTime(shortDateFormat.parse(fileName.substring(0, SHORT_DATE_LENGTH)));
            }
        } catch (final ParseException e) {
            LOGGER.warn("invalid date on file name: " + fileNamePath);
            throw e;
        }

        return calendar;
    }

    /**
     * Return the offset from UTC set in this file name
     *
     * @param fileNamePath
     *            The file name
     * @return The offset in milliseconds from UTC
     */
    public static long getUTCOffset(final String fileNamePath) {
        String fileName = getFromDatePart(fileNamePath);

        final int underscoreLocation = fileName.indexOf('_');
        try {
            if (underscoreLocation > SHORT_DATE_UNDERSCORE_POS) {
                fileName = fileName.substring(13, 18);
                final char character = fileName.charAt(0);
                fileName = fileName.substring(1);
                final int hour = Integer.valueOf(fileName.substring(0, 2));
                final int minute = Integer.valueOf(fileName.substring(2));
                if (character == '+') {
                    return +(hour * 60 * 60 * 1000 + minute * 60 * 1000);
                } else if (character == '-') {
                    return -(hour * 60 * 60 * 1000 + minute * 60 * 1000);
                } else {
                    throw new IllegalStateException();
                }
            }
            return 0;
        } catch (final Exception e) {
            LOGGER.warn("invalid date on file name: " + fileNamePath);
            return 0;
        }
    }

    /**
     * Return the offset from UTC set in this UTC offset string
     *
     * @param utcOffsetString
     *            The UTC offset string in format [+|-]HH:MM format
     * @return The offset in milliseconds from UTC
     * @throws InvalidTimeException
     *             on string format errors
     */
    public static long getUTCOffsetStandard(final String utcOffsetString) throws InvalidTimeException {
        String workingString = utcOffsetString.replaceAll("\\s", "");

        workingString = workingString.replaceAll("\\+", "");

        final String[] splitString = workingString.split(":");

        final int hour = Integer.parseInt(splitString[0]);
        final int minute = splitString.length > 1 ? Integer.parseInt(splitString[1]) : 0;

        if (hour > 24 || hour < -24) {
            throw new InvalidTimeException("invalid hour value: " + hour);
        }

        if (minute > 60) {
            throw new InvalidTimeException("invalid minute value: " + minute);
        }

        if (hour >= 0) {
            return hour * 60 * 60 * 1000 + minute * 60 * 1000;
        }
        return hour * 60 * 60 * 1000 - minute * 60 * 1000;
    }

    /**
     * This method recurses across and down a directory and returns a list of files that matches the file name filter. If the filter is null, all
     * files are returned (Code is a modified version of the code at http://snippets.dzone.com/posts/show/1875)
     *
     * @param directory
     *            The directory to search
     * @param filter
     *            The filter
     * @return The list of files
     */
    public static List<File> listFiles(final File directory, final FilenameFilter filter) {
        final List<File> fileList = new ArrayList<>();

        final File[] directoryEntries = directory.listFiles();

        for (final File entry : directoryEntries) {
            if (filter == null || filter.accept(directory, entry.getName())) {
                fileList.add(entry);
            }

            if (entry.isDirectory()) {
                fileList.addAll(listFiles(entry, filter));
            }
        }

        return fileList;
    }

    /**
     * This method copies two files
     *
     * @param sourceFile
     *            The source file
     * @param destFile
     *            The destination file
     * @throws IOException
     *             - exception
     */
    public static void copyfile(final File sourceFile, final File destFile) throws IOException {
        final BufferedReader inReader = new BufferedReader(new FileReader(sourceFile));
        final PrintWriter fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(destFile)));

        for (String line = inReader.readLine(); line != null; line = inReader.readLine()) {
            fileWriter.println(line);
        }

        inReader.close();
        fileWriter.close();
    }

    /**
     * This method restores the serialised object from the file specified.
     *
     * @param filename
     *            - An absolute path of the serialised object file
     * @return an instance of the java.lang.Object
     */
    public static Object restoreObject(final String filename) {
        final Object readObject;
        try {
            final ObjectInputStream obj = new ObjectInputStream(new FileInputStream(filename));
            readObject = obj.readObject();
            obj.close();

        } catch (final Exception e) {
            throw new ObjectRestorationException("Failed to restore the process configuration data, Reason:\n" + e.getMessage());
        }
        return readObject;
    }

    /**
     * Get the file name from the start of the date
     *
     * @param fileNamePath
     *            : The incoming full name
     * @return The date part
     */
    private static String getFromDatePart(final String fileNamePath) {
        if (fileNamePath.charAt(0) == 'A') {
            return fileNamePath.substring(1);
        }

        int startPos = fileNamePath.lastIndexOf("%~%A");
        if (startPos >= 0) {
            return fileNamePath.substring(startPos + 4);
        }

        startPos = fileNamePath.lastIndexOf(File.separator + "A");
        if (startPos >= 0) {
            return fileNamePath.substring(startPos + 2);
        }

        return fileNamePath;
    }
}

/**
 * @author aia
 */
class ObjectRestorationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Default.
     *
     * @param message
     *            - message
     */
    ObjectRestorationException(final String message) {
        super(message);
    }

}
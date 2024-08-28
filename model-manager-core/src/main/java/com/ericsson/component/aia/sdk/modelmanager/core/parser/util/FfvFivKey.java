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

import java.util.regex.Pattern;

/**
 * @author aia
 */
public class FfvFivKey implements Comparable<FfvFivKey> {

    private static final String DELIMITER = "/";

    private static final String NUMBER_ONLY_REGEX = "\\d+";

    private static final Pattern STRING_NUMBERIC_SPILTTER = Pattern.compile("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

    private String iteration;

    private String fileFormatVersion;

    private String fileInformationVersion;

    /**
     * @param docNo
     *            - document number
     * @param fileFormatVersion
     *            - file format
     * @param fileInformationVersion
     *            - file version
     */
    public FfvFivKey(final String docNo, final String fileFormatVersion, final String fileInformationVersion) {
        this.iteration = getIteration(docNo);
        this.fileFormatVersion = fileFormatVersion;
        this.fileInformationVersion = fileInformationVersion;
    }

    private String getIteration(final String docNo) {
        if (docNo == null) {
            return null;
        }
        final String iterationNumber = docNo.substring(docNo.indexOf(DELIMITER) + 1);

        return iterationNumber.matches(NUMBER_ONLY_REGEX) ? iterationNumber : null;
    }

    public String getIteration() {
        return iteration;
    }

    public void setIteration(final String iteration) {
        this.iteration = iteration;
    }

    public String getFileFormatVersion() {
        return fileFormatVersion;
    }

    public void setFileFormatVersion(final String fileFormatVersion) {
        this.fileFormatVersion = fileFormatVersion;
    }

    public String getFileInformationVersion() {
        return fileInformationVersion;
    }

    public void setFileInformationVersion(final String fileInformationVersion) {
        this.fileInformationVersion = fileInformationVersion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fileFormatVersion == null) ? 0 : fileFormatVersion.hashCode());
        result = prime * result + ((fileInformationVersion == null) ? 0 : fileInformationVersion.hashCode());
        result = prime * result + ((iteration == null) ? 0 : iteration.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof FfvFivKey)) {
            return false;
        }

        final FfvFivKey other = (FfvFivKey) obj;

        return isEqual(iteration, other.iteration) && isEqual(fileFormatVersion, other.fileFormatVersion)
                && isEqual(fileInformationVersion, other.fileInformationVersion);

    }

    private boolean isEqual(final String first, final String second) {
        if (first == null ^ second == null) {
            return false;
        }
        if (first == null && second == null) {
            return true;
        }
        return first.equals(second);
    }

    @Override
    public int compareTo(final FfvFivKey other) {
        int result = nullSafeStringComparator(this.iteration, other.iteration);

        if (result != 0) {
            return result;
        }
        result = nullSafeStringComparator(this.fileFormatVersion, other.fileFormatVersion);
        if (result != 0) {
            return result;
        }
        return nullSafeStringComparator(this.fileInformationVersion, other.fileInformationVersion);
    }

    private int nullSafeStringComparator(final String first, final String second) {
        if (first == null ^ second == null) {
            return (first == null) ? -1 : 1;
        }

        if (first == null && second == null) {
            return 0;
        }

        return compare(first, second);
    }

    @Override
    public String toString() {
        return "iteration : '" + iteration + "', fileFormatVersion : '" + fileFormatVersion + "', fileInformationVersion : '" + fileInformationVersion
                + "'";
    }

    private int compare(final String firstString, final String secondString) {
        final String[] firstObjectArray = STRING_NUMBERIC_SPILTTER.split(firstString);
        final String[] secondObjectArray = STRING_NUMBERIC_SPILTTER.split(secondString);

        for (int i = 0; i < firstObjectArray.length && i < secondObjectArray.length; ++i) {
            if (Character.isDigit(firstObjectArray[i].charAt(0)) && Character.isDigit(secondObjectArray[i].charAt(0))) {
                final int firstInt = Integer.parseInt(firstObjectArray[i]);
                final int secondInt = Integer.parseInt(secondObjectArray[i]);

                final int result = firstInt - secondInt;
                if (result != 0) {
                    return result < 0 ? -1 : 1;
                }

            } else {
                final int result = firstObjectArray[i].compareToIgnoreCase(secondObjectArray[i]);
                if (result != 0) {
                    return result < 0 ? -1 : 1;
                }
            }
        }
        return 0;

    }

}

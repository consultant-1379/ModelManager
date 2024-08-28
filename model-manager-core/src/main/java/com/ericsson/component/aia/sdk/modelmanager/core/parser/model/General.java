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

package com.ericsson.component.aia.sdk.modelmanager.core.parser.model;

import org.jdom.Element;
import org.jdom.Namespace;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.FfvFivKey;

/**
 * This class is used to hold a single general entry read from an event schema XML file
 */
public class General {
    private String docNo;

    private String revision;

    private String date;

    private String author;

    private String fileFormatVersion;

    private String fileInformationVersion;

    /**
     * Instantiate a general entry as an object
     *
     * @param general
     *            the general XML element
     * @param namespace
     *            the name space to use for reading elements
     * @throws SchemaException
     *             - exception
     */
    protected General(final Element general, final Namespace namespace) throws SchemaException {
        try {
            docNo = general.getChild("docno", namespace).getText().trim();
            revision = general.getChild("revision", namespace).getText().trim();
            date = general.getChild("date", namespace).getText().trim();
            author = general.getChild("author", namespace).getText().trim();
            fileFormatVersion = general.getChild("ffv", namespace).getText().trim();

            final Element namespaceE = general.getChild("fiv", namespace);
            if (namespaceE != null) {
                fileInformationVersion = namespaceE.getText().trim();
            } else {
                fileInformationVersion = revision;
            }
        } catch (final Exception e) {
            throw new SchemaException("Error parsing event schema element [" + general.getName() + "] " + e);
        }
    }

    /**
     * Return the document number
     *
     * @return the document number
     */
    public String getDocNo() {
        return docNo;
    }

    /**
     * Return the revision
     *
     * @return the revision
     */
    public String getRevision() {
        return revision;
    }

    /**
     * Return the date
     *
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * Return the author
     *
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Get the file format version supported by this schema
     *
     * @return the file format version
     */
    public String getFileFormatVersion() {
        return fileFormatVersion;
    }

    /**
     * Get the file information version supported by this schema
     *
     * @return the file information version
     */
    public String getFileInformationVersion() {
        return fileInformationVersion;
    }

    /**
     * Get the file version key of this schema
     *
     * @return the file format version and the file information version
     */
    public String getFileVersionKey() {
        return fileFormatVersion + "_" + fileInformationVersion;
    }

    /**
     * Get file version key including docno, fileFormatVersion and FileInformationVersion for this schema
     *
     * @return key
     */
    public FfvFivKey getFfvFivKey() {
        return new FfvFivKey(docNo, fileFormatVersion, fileInformationVersion);
    }

    /**
     * return a string representation of this General item
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        return "General [docNo=" + docNo + ", revision=" + revision + ", date=" + date + ", author=" + author + "]";
    }
}

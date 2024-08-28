/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.sdk.modelmanager;

import java.util.Collection;

/**
 * Interface for the basic query operations performed by the model manager.
 */
public interface ModelManager {

    /**
     * Gets a parser from database (if XML).
     *
     * @param parserId
     *            - the parserId of the parser
     * @return the corresponding parser
     */
    String getParser(final String parserId);

    /**
     * Gets a parser based on name version and type
     *
     * @param name
     *            - parser name
     * @param type
     *            - parser type
     * @return the parser XML
     */
    String getParser(final String name, final String type);

    /**
     * Deletes a parser from database (if XML).
     *
     * @param parserId
     *            - the parserId of the parser
     */
    void deleteParser(final String parserId);

    /**
     * Adds a parser to to database (if XML).
     *
     * @param name
     *            - name of the parser
     * @param type
     *            - type of the parser
     * @param parser
     *            - XML
     * @return the parser unique ID.
     */
    String addParser(final String name, final String type, final String parser);

    /**
     * Updates a parser into database (if XML).
     *
     * @param parserId
     *            - the parserId of the parser
     * @param parser
     *            - the corresponding parser XML
     */
    void updateParser(final String parserId, final String parser);

    /**
     * Get parsers names by type
     *
     * @param type
     *            - type of the parser
     * @return a collection with all the parsers names for this type.
     */
    Collection<String> listParsersByType(final String type);

    /**
     * Get parsers types
     *
     * @return a collection with all the parser types.
     */
    Collection<String> listAllParserTypes();

    /**
     * Validates the XML
     *
     * @param xml
     *            - xml
     * @return true or false
     */
    boolean validate(String xml);

}

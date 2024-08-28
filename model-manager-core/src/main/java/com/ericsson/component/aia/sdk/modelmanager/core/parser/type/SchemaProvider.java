/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.sdk.modelmanager.core.parser.type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.ResourceNotFoundException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.Schema;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.FfvFivKey;

/**
 * @author aia
 */
public interface SchemaProvider {

    Logger LOGGER = LoggerFactory.getLogger(SchemaProvider.class);

    /**
     * @return schema
     * @throws ResourceNotFoundException
     *             - exception
     * @throws SchemaException
     *             - exception
     */
    SchemaProvider load() throws ResourceNotFoundException, SchemaException;

    /**
     * @param docNo
     *            Document no
     * @param ffv
     *            File Format Version
     * @param fiv
     *            File Information Version
     * @return matched Schema
     */
    Schema getSchema(final String docNo, final String ffv, final String fiv);

    /**
     * @param ffvFivKey
     *            -ffvFivKey
     * @return matched Schema
     */
    Schema getSchema(final FfvFivKey ffvFivKey);

    /**
     * @param docNo
     *            Document no
     * @param ffv
     *            File Format Version
     * @param fiv
     *            File Information Version
     * @return closest Schema
     */
    Schema getTreatAsSchema(final String docNo, final String ffv, final String fiv);

    /**
     * @param ffvFivKey
     *            -ffvFivKey
     * @return closest Schema
     */
    Schema getTreatAsSchema(final FfvFivKey ffvFivKey);

    /**
     * @return int
     */
    int getIdLength();

    /**
     * @return int
     */
    int getIdStartPos();

    /**
     * @return boolean
     */
    boolean isIdInEvent();

}

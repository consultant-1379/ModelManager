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
package com.ericsson.component.aia.sdk.modelmanager.core.parser.type;

import java.util.NavigableMap;
import java.util.TreeMap;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.ResourceNotFoundException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.handler.SchemaTypeHandler;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.handler.SchemaTypeLoader;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.Schema;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.FfvFivKey;

/**
 * @author aia
 */
public abstract class SchemaHandler implements SchemaProvider {

    protected final NavigableMap<FfvFivKey, Schema> schemaMap;

    private final SchemaEnum neType;

    private SchemaTypeHandler schemaTypeHandler;

    /**
     * Default.
     *
     * @param neType
     *            - ne type
     */
    public SchemaHandler(final SchemaEnum neType) {
        this.neType = neType;
        this.schemaMap = new TreeMap<FfvFivKey, Schema>();
    }

    /**
     * @param neType
     *            - type
     * @return schema
     * @throws ResourceNotFoundException
     *             - exception
     * @throws SchemaException
     *             - exception
     */
    private SchemaTypeHandler getSchemaTypeHandler(final SchemaEnum neType) throws ResourceNotFoundException, SchemaException {
        final SchemaTypeLoader schemaTypeLoader = new SchemaTypeLoader();
        return schemaTypeLoader.getSchemaTypeMap().get(neType);
    }

    /**
     * @return load Schema Type Xml and Schema xml files
     * @throws ResourceNotFoundException
     * @throws SchemaException
     */
    @Override
    public SchemaProvider load() throws ResourceNotFoundException, SchemaException {
        this.schemaTypeHandler = getSchemaTypeHandler(neType);
        return this;
    }

    @Override
    public int getIdLength() {
        return schemaTypeHandler.getIdLength();
    }

    @Override
    public int getIdStartPos() {
        return schemaTypeHandler.getIdStartPos();
    }

    @Override
    public boolean isIdInEvent() {
        return schemaTypeHandler.isIdInEvent();
    }

    /**
     * @return the schemaTypeHandler
     */
    public SchemaTypeHandler getSchemaTypeHandler() {
        return schemaTypeHandler;
    }

    /**
     * @return the neType
     */
    public SchemaEnum getNeType() {
        return neType;
    }
}

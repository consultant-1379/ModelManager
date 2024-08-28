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

import java.io.IOException;

import org.jaxen.JaxenException;
import org.jdom.JDOMException;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.ResourceNotFoundException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.Schema;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.SchemaBuilder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.FfvFivKey;

/**
 * @author aia
 */
public class FileBasedSchemaHandler extends SchemaHandler {

    /**
     * @param neType
     *            - ne type
     */
    public FileBasedSchemaHandler(final SchemaEnum neType) {
        super(neType);
    }

    @Override
    /**
     * Loads
     */
    public SchemaProvider load() throws ResourceNotFoundException, SchemaException {
        super.load();
        for (final String filePath : getSchemaTypeHandler().getXmlFilesSet()) {
            try {
                final Schema schema = new SchemaBuilder(getSchemaTypeHandler().getName(), filePath)
                        .paramPreamble(getSchemaTypeHandler().getParamPreamble()).valuePreamble(getSchemaTypeHandler().getValuePreamble()).build();
                schemaMap.put(schema.getGeneralHandler().getGeneralInfo().getFfvFivKey(), schema);
            } catch (JaxenException | SchemaException | JDOMException | IOException exception) {
                LOGGER.error("loading of schema of type {} from file {} failed ", getSchemaTypeHandler().getName(), filePath, exception);
            }
        }
        return this;
    }

    @Override
    public Schema getSchema(final String docNo, final String ffv, final String fiv) {
        final FfvFivKey ffvFivKey = new FfvFivKey(docNo, ffv, fiv);
        return getSchema(ffvFivKey);
    }

    @Override
    public Schema getSchema(final FfvFivKey ffvFivKey) {
        return schemaMap.get(ffvFivKey);
    }

    @Override
    public Schema getTreatAsSchema(final String docNo, final String ffv, final String fiv) {
        final FfvFivKey ffvFivKey = new FfvFivKey(docNo, ffv, fiv);
        return getTreatAsSchema(ffvFivKey);
    }

    @Override
    public Schema getTreatAsSchema(final FfvFivKey ffvFivKey) {
        return getClosestSchema(ffvFivKey);
    }

    /**
     * @param fileVersionKey
     *            - fileVersionKey
     * @return schema
     */
    protected Schema getClosestSchema(final FfvFivKey fileVersionKey) {
        Schema closestSchema = null;
        final FfvFivKey floorVersionKey = schemaMap.floorKey(fileVersionKey);
        if (floorVersionKey != null && fileVersionKey != null) {
            closestSchema = schemaMap.get(floorVersionKey);
            if (!fileVersionKey.getFileFormatVersion().equals(floorVersionKey.getFileFormatVersion())) {
                LOGGER.info("The closest match found for : ({}), but its ffv : '{}' does not match ffv :'{}'", floorVersionKey.toString(),
                        floorVersionKey.getFileFormatVersion(), fileVersionKey.getFileFormatVersion());
                return null;
            }
        }
        LOGGER.info("Will treat fiv/ffv ({})  as ({})", fileVersionKey.toString(), floorVersionKey.toString());
        return closestSchema;
    }
}

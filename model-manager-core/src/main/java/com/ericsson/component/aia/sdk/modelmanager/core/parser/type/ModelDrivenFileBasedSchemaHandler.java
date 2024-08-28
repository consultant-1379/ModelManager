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
package com.ericsson.component.aia.sdk.modelmanager.core.parser.type;

import static com.ericsson.component.aia.sdk.modelmanager.core.parser.type.FileBasedSchemaHandlerConstants.CHECK_FOR_SCHEMAS_INTERVAL;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.type.FileBasedSchemaHandlerConstants.MODELS_FILE_PATH;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.jaxen.JaxenException;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.Schema;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.SchemaBuilder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.FfvFivKey;

/**
 * @author aia
 */
public class ModelDrivenFileBasedSchemaHandler extends FileBasedSchemaHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelDrivenFileBasedSchemaHandler.class);

    private final String modelsFilepath;

    private final long checkForSchemasIntervalInMiliSec;

    private long timeToCheckForNewSchemas = 0L;

    /**
     * @param neType
     *            - ne type
     */
    public ModelDrivenFileBasedSchemaHandler(final SchemaEnum neType) {
        super(neType);
        this.modelsFilepath = ModelDrivenUtils.getProperty(MODELS_FILE_PATH);
        this.checkForSchemasIntervalInMiliSec = ModelDrivenUtils.getLongProperty(CHECK_FOR_SCHEMAS_INTERVAL);
    }

    @Override
    public Schema getTreatAsSchema(final FfvFivKey ffvFivKey) {
        final long currentTime = System.currentTimeMillis();
        LOGGER.debug("currentTime is: {} and timeToCheckForNewSchemas is: {}", currentTime, timeToCheckForNewSchemas);
        if (currentTime >= timeToCheckForNewSchemas && schemaAvailable()) {
            final Schema newSchema = getSchema(ffvFivKey);
            if (newSchema != null) {
                LOGGER.info("new schema available for FfvFivKey {} from {} so using it.", ffvFivKey, newSchema.getAvroPackageName());
                return getSchema(ffvFivKey);
            }
        }
        if (currentTime >= timeToCheckForNewSchemas) {
            timeToCheckForNewSchemas = currentTime + checkForSchemasIntervalInMiliSec;
        }
        return getClosestSchema(ffvFivKey);
    }

    private boolean schemaAvailable() {
        final List<String> schemaFilePaths = getNewSchemasFromFilepath();
        if (!schemaFilePaths.isEmpty()) {
            loadNewSchemas(schemaFilePaths);
            return true;
        }
        return false;
    }

    private List<String> getNewSchemasFromFilepath() {
        final List<String> newSchemaFilePaths = ModelDrivenUtils.getFiles(Paths.get(modelsFilepath));
        LOGGER.debug("number of files found in model filepath {} is: {}", modelsFilepath, newSchemaFilePaths.size());
        return newSchemaFilePaths;
    }

    private void loadNewSchemas(final List<String> filePaths) {
        for (final String filePath : filePaths) {
            try {
                final Schema schema = new SchemaBuilder(getSchemaTypeHandler().getName(), filePath)
                        .paramPreamble(getSchemaTypeHandler().getParamPreamble()).valuePreamble(getSchemaTypeHandler().getValuePreamble()).build();
                if (!schemaMap.containsKey(schema.getGeneralHandler().getGeneralInfo().getFfvFivKey())) {
                    LOGGER.debug("Added new Schema from {} to the map", filePath);
                    schemaMap.put(schema.getGeneralHandler().getGeneralInfo().getFfvFivKey(), schema);
                }
            } catch (JaxenException | SchemaException | JDOMException | IOException exception) {
                LOGGER.error("loading of schema of type {} from file {} failed ", getSchemaTypeHandler().getName(), filePath, exception);
            }
        }
    }
}

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

import static com.ericsson.component.aia.sdk.modelmanager.core.parser.type.FileBasedSchemaHandlerConstants.CHECK_FOR_SCHEMAS_INTERVAL;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.type.FileBasedSchemaHandlerConstants.MODELS_FILE_PATH;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jaxen.JaxenException;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.ResourceNotFoundException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.Schema;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.SchemaBuilder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.FfvFivKey;

/**
 * This class will load the model XML files from a location on the file system. Only the files needed by the parser will be loaded. If there are other
 * files on the file system that are not needed, these will not be loaded.
 */
public class ModelDrivenFileBasedLazySchemaHandler extends SchemaHandler {

    private static final String REGEX_FOR_MODEL_XML_FILES = "([0-9]*)_*([a-z0-9]*)_([a-z0-9]*).xml";

    private static final Pattern FILES_NAME_PATTERN = Pattern.compile(REGEX_FOR_MODEL_XML_FILES, Pattern.CASE_INSENSITIVE);

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelDrivenFileBasedLazySchemaHandler.class);

    protected NavigableMap<FfvFivKey, String> fileMap = new TreeMap<FfvFivKey, String>();

    private final String modelsFilepath;

    private final long checkForSchemasIntervalInMiliSec;

    private long timeToCheckForNewSchemas = 0L;

    /**
     * @param neType
     *            - ne type
     */
    public ModelDrivenFileBasedLazySchemaHandler(final SchemaEnum neType) {
        super(neType);
        this.modelsFilepath = ModelDrivenUtils.getProperty(MODELS_FILE_PATH);
        this.checkForSchemasIntervalInMiliSec = ModelDrivenUtils.getLongProperty(CHECK_FOR_SCHEMAS_INTERVAL);
    }

    @Override
    public SchemaProvider load() throws ResourceNotFoundException, SchemaException {
        super.load();
        populateFileMap();
        return this;
    }

    private void populateFileMap() {
        for (final String filePath : ModelDrivenUtils.getFiles(Paths.get(modelsFilepath))) {
            final Matcher matcher = FILES_NAME_PATTERN.matcher(filePath.substring(filePath.lastIndexOf(File.separator) + 1));
            if (matcher.matches()) {
                final FfvFivKey key = new FfvFivKey(matcher.group(1), matcher.group(2), matcher.group(3));
                fileMap.put(key, filePath);
            } else {
                LOGGER.error("The file {} has invalid name pattern, it can not be used as a schema definition file.", filePath);
            }
        }
    }

    @Override
    public Schema getSchema(final FfvFivKey ffvFivKey) {
        Schema schema = null;
        if (schemaMap.containsKey(ffvFivKey)) {
            schema = schemaMap.get(ffvFivKey);
        } else {
            final String filePath = fileMap.get(ffvFivKey);
            if (filePath != null) {
                schema = createSchemas(filePath);
            } else {
                schema = rePopulateFileMapAndGetSchemas(ffvFivKey);
            }
        }
        return schema;
    }

    @Override
    public Schema getTreatAsSchema(final FfvFivKey ffvFivKey) {
        Schema schema = null;
        final long currentTime = System.currentTimeMillis();
        LOGGER.debug("currentTime is: {} and timeToCheckForNewSchemas is: {}", currentTime, timeToCheckForNewSchemas);
        if (currentTime >= timeToCheckForNewSchemas) {
            final Schema newSchema = getSchema(ffvFivKey);
            if (newSchema != null) {
                LOGGER.info("new schema available for FfvFivKey {} from {} so using it.", ffvFivKey, newSchema.getAvroPackageName());
                schema = newSchema;
            }
            timeToCheckForNewSchemas = currentTime + checkForSchemasIntervalInMiliSec;
        }

        if (schema == null) {
            schema = getClosestSchema(ffvFivKey);
        }

        return schema;
    }

    private Schema rePopulateFileMapAndGetSchemas(final FfvFivKey ffvFivKey) {
        Schema schema = null;
        //Re-check to see if file exists now on file system that did not exist previously
        populateFileMap();
        final String filePath = fileMap.get(ffvFivKey);
        if (filePath != null) {
            schema = createSchemas(filePath);
        }
        return schema;
    }

    private Schema createSchemas(final String filePath) {
        Schema schema = null;
        try {
            schema = new SchemaBuilder(getSchemaTypeHandler().getName(), filePath).paramPreamble(getSchemaTypeHandler().getParamPreamble())
                    .valuePreamble(getSchemaTypeHandler().getValuePreamble()).build();
            schemaMap.put(schema.getGeneralHandler().getGeneralInfo().getFfvFivKey(), schema);
        } catch (JaxenException | SchemaException | JDOMException | IOException exception) {
            LOGGER.error("loading of schema of type {} from file {} failed ", SchemaEnum.CELLTRACE, filePath, exception);
        }
        return schema;
    }

    private Schema getClosestSchema(final FfvFivKey fileVersionKey) {
        Schema closestSchema = null;
        final FfvFivKey floorVersionKey = fileMap.floorKey(fileVersionKey);
        if (floorVersionKey != null && fileVersionKey != null) {
            if (!fileVersionKey.getFileFormatVersion().equals(floorVersionKey.getFileFormatVersion())) {
                LOGGER.info("The closest match found for : ({}), but its ffv : '{}' does not match ffv :'{}'", floorVersionKey.toString(),
                        floorVersionKey.getFileFormatVersion(), fileVersionKey.getFileFormatVersion());
            } else {
                closestSchema = getSchema(floorVersionKey);
                LOGGER.info("Will treat fiv/ffv ({})  as ({})", fileVersionKey.toString(), floorVersionKey.toString());
            }
        }
        return closestSchema;
    }

    @Override
    public Schema getSchema(final String docNo, final String ffv, final String fiv) {
        final FfvFivKey ffvFivKey = new FfvFivKey(docNo, ffv, fiv);
        return getSchema(ffvFivKey);
    }

    @Override
    public Schema getTreatAsSchema(final String docNo, final String ffv, final String fiv) {
        final FfvFivKey ffvFivKey = new FfvFivKey(docNo, ffv, fiv);
        return getTreatAsSchema(ffvFivKey);
    }

    protected NavigableMap<FfvFivKey, String> getFileMap() {
        return fileMap;
    }

    protected NavigableMap<FfvFivKey, Schema> getSchemaMap() {
        return schemaMap;
    }
}
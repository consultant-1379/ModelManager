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

import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.SchemaUtil.DEFAULT_EVENT_CLASS_PACKAGE;
import static com.ericsson.component.aia.sdk.modelmanager.core.parser.util.SchemaUtil.RESOURCES_PARENT_DIRECTORY;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.ResourceNotFoundException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.handler.SchemaTypeHandler;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.Event;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.Schema;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.type.FileBasedSchemaHandler;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.type.SchemaEnum;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.type.SchemaProvider;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.DirectoryUtil;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.FfvFivKey;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.JavaClassGenerator;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.util.MappedEvent;

/**
 * This class generates a java package for each schema and a java class for each event.
 *
 */
public class GenerateEventBeans {

    private static final Logger LOG = LoggerFactory.getLogger(GenerateEventBeans.class);

    /**
     * @param args
     *            - args
     */
    protected void generate(final String[] args) {
        checkArguments(args);

        final File metadataDir = new File(args[0] + File.separator + RESOURCES_PARENT_DIRECTORY);
        final File fileDir = new File(args[1]);

        try {
            new DirectoryUtil(metadataDir, fileDir).initializeDirectoryStructure();
        } catch (final IOException e1) {
            e1.printStackTrace();
            throw new GenerationException(e1);
        }

        addDirectoryToClasspath(args[0]);

        if (isEventBeanGenerationRequired(metadataDir, fileDir)) {
            try {
                generateEvents(args);
            } catch (SchemaException | IOException e) {
                e.printStackTrace();
                throw new GenerationException(e);
            }
        }
    }

    /**
     * This method generates the schema packages and event classes usage: GenerateEventBeans config_dir output_dir
     *
     * @param args
     *            Command line arguments (args[0] should be Resource directory and args[1] should be output package directory)
     */
    public static void main(final String[] args) {
        new GenerateEventBeans().generate(args);
    }

    /**
     * @param args
     *            - args
     * @throws SchemaException
     *             - exception
     * @throws IOException
     *             - exception
     */
    protected void generateEvents(final String[] args) throws SchemaException, IOException {
        final String outputDir = args[1];
        final String eventBeanPackage = DEFAULT_EVENT_CLASS_PACKAGE;
        final File ebPackageDir = new File(outputDir + '/' + eventBeanPackage.replaceAll("\\.", "/"));
        defineDirectoryForEventClassPackage(ebPackageDir);

        for (final SchemaEnum schemaType : SchemaEnum.values()) {
            final PojoGenSchemaHandler schemaHandler = getSchemaHandler(schemaType);
            if (schemaHandler.getSchemaTypeHandler() != null) {
                generateBaseClasses(ebPackageDir, schemaType, schemaHandler.getEventMap(), eventBeanPackage);
                generateSubClasses(ebPackageDir, schemaType, schemaHandler.getSchemaMap(), eventBeanPackage);
            }
        }
    }

    /**
     * @param schemaType
     *            - type
     * @return pojo
     * @throws ResourceNotFoundException
     *             - exception
     * @throws SchemaException
     *             - exception
     */
    protected PojoGenSchemaHandler getSchemaHandler(final SchemaEnum schemaType) throws ResourceNotFoundException, SchemaException {
        return (PojoGenSchemaHandler) new PojoGenSchemaHandler(schemaType).load();
    }

    /**
     * @param ebPackageDir
     *            - package
     * @param schemaType
     *            - type
     * @param schemaMap
     *            - map
     * @param eventBeanPackage
     *            - bean
     * @throws IOException
     *             - exception
     */
    void generateSubClasses(final File ebPackageDir, final SchemaEnum schemaType, final Map<FfvFivKey, Schema> schemaMap,
                            final String eventBeanPackage)
            throws IOException {
        for (final FfvFivKey schemaKey : schemaMap.keySet()) {

            final Schema schema = schemaMap.get(schemaKey);
            final String schemaName = schema.getPackageName();

            final File schemaPackageDir = new DirectoryUtil().createFolder(ebPackageDir, schemaName);

            for (final Event event : schema.getEventHandler().getMap().values()) {
                final JavaClassGenerator generator = getJavaClassGenerator();
                generator.generateSubJavaClass(schemaPackageDir, schemaType, event, eventBeanPackage);
            }

            LOG.info("event classes generated for schema {}", schemaName);
        }
    }

    /**
     * @return class
     */
    protected JavaClassGenerator getJavaClassGenerator() {
        return new JavaClassGenerator();
    }

    private void generateBaseClasses(final File ebPackageDir, final SchemaEnum schemaType, final Map<Event, MappedEvent> eventMap,
                                     final String eventBeanPackage)
            throws IOException {
        final File basePackageDir = new DirectoryUtil().createFolder(ebPackageDir, schemaType.value());

        for (final MappedEvent mappedEvent : eventMap.values()) {
            final JavaClassGenerator generator = getJavaClassGenerator();
            generator.generateBaseJavaClass(basePackageDir, mappedEvent, eventBeanPackage);
        }
    }

    private void defineDirectoryForEventClassPackage(final File ebPackageDir) {
        try {
            new DirectoryUtil().createDirectoryIfNotExists(ebPackageDir);
        } catch (final IOException e) {
            LOG.warn("{} file already exists will be used / failed to create directory", ebPackageDir.getAbsoluteFile());
        }
    }

    /**
     * @param metadataDirName
     *            - directory name
     */
    protected void addDirectoryToClasspath(final String metadataDirName) {
        GenerateUtils.addDirectoryToClasspath(new File(metadataDirName));
    }

    /**
     * @param metadataDir
     *            - folder
     * @param fileDir
     *            - file
     * @return boolean
     */
    protected boolean isEventBeanGenerationRequired(final File metadataDir, final File fileDir) {
        if (GenerateUtils.generateRequired(metadataDir, fileDir)) {
            LOG.info("generation required: files in {} newer than some files in {}", metadataDir.getAbsolutePath(), fileDir.getName());
            return true;
        }
        LOG.info("generation not required: all files in {} older than each file in {}", metadataDir.getAbsolutePath(), fileDir.getName());
        return false;

    }

    /**
     * @param args
     *            - args
     */
    public void checkArguments(final String[] args) {
        if (args.length != 4) {
            throw new IllegalArgumentException("usage: GenerateEventBeans config_dir output_dir schema_xml_file schema_xsd_file");
        }

        LOG.debug("GenerateEventBeans arguments: {}, {}, {}, {}, {}", args[0], args[1], args[2], args[3]);
    }

    /**
     * @author aia
     */
    class PojoGenSchemaHandler extends FileBasedSchemaHandler {

        private final Map<Event, MappedEvent> eventMap = new TreeMap<Event, MappedEvent>();

        /**
         * @param neType
         *            - ne type
         */
        PojoGenSchemaHandler(final SchemaEnum neType) {
            super(neType);
        }

        @Override
        public SchemaProvider load() throws ResourceNotFoundException, SchemaException {
            super.load();
            buildEventParameterMap();
            return this;
        }

        /**
         * Build the event parameter map using the rules described in the comment at the top of this class
         */
        private void buildEventParameterMap() {
            for (final Schema schema : schemaMap.descendingMap().values()) {
                for (final Event event : schema.getEventHandler().getMap().values()) {

                    MappedEvent mappedEvent = eventMap.get(event);

                    if (mappedEvent == null) {
                        mappedEvent = new MappedEvent(event);
                        eventMap.put(event, mappedEvent);
                    }
                    mappedEvent.setParameters(event);
                }
            }
        }

        /**
         * @return the eventMap
         */
        public Map<Event, MappedEvent> getEventMap() {
            return eventMap;
        }

        public Map<FfvFivKey, Schema> getSchemaMap() {
            return schemaMap;
        }

        @Override
        public SchemaTypeHandler getSchemaTypeHandler() {
            return super.getSchemaTypeHandler();
        }
    }

}

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
package com.ericsson.component.aia.sdk.modelmanager.core.services;

import static com.ericsson.component.aia.sdk.modelmanager.core.config.ModelManagerConfiguration.modelCatalogName;
import static com.ericsson.component.aia.sdk.modelmanager.core.schema.ModelSchema.DOCUMENT;
import static com.ericsson.component.aia.sdk.modelmanager.core.schema.ModelSchema.KEY;
import static com.ericsson.component.aia.sdk.modelmanager.core.schema.ModelSchema.NAME;
import static com.ericsson.component.aia.sdk.modelmanager.core.schema.ModelSchema.TYPE;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.ericsson.aia.metadata.api.MetaDataServiceIfc;
import com.ericsson.aia.metadata.exception.MetaDataServiceException;
import com.ericsson.aia.metadata.filter.builder.FilterBuilder;
import com.ericsson.aia.metadata.filter.builder.model.Operator;
import com.ericsson.component.aia.sdk.modelmanager.ModelManager;
import com.ericsson.component.aia.sdk.modelmanager.core.exception.ParserAlreadyExistsException;
import com.ericsson.component.aia.sdk.modelmanager.core.util.ModelManagerUtil;
import com.ericsson.component.aia.sdk.modelmanager.exceptions.ModelManagerException;
import com.ericsson.component.aia.sdk.modelmanager.exceptions.ModelManagerExceptionCodes;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class provides implementation of {@link ModelManager} operations.
 *
 * @author ezsalro
 *
 */
public class ModelManagerImpl implements ModelManager {

    private static final Logger LOG = LoggerFactory.getLogger(ModelManagerImpl.class);

    private final MetaDataServiceIfc metaDataService;
    private final ObjectMapper mapper;

    /**
     * Default constructor.
     *
     * @param metaDataService
     *            - service for storing data into database
     */
    public ModelManagerImpl(final MetaDataServiceIfc metaDataService) {
        this.metaDataService = metaDataService;
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public String getParser(final String parserId) {

        try {
            return metaDataService.get(modelCatalogName, parserId, DOCUMENT);
        } catch (final MetaDataServiceException e) {
            LOG.error("Error retrieving parser by id {}", parserId, e);
        }

        return null;

    }

    @Override
    public String getParser(final String name, final String type) {
        try {
            final Optional<Map<String, Object>> collection = this.metaDataService.findByFilter(modelCatalogName,
                    FilterBuilder.newMongoDBFilter().addProperty(TYPE, type).addProperty(NAME, name).build(), Arrays.asList(DOCUMENT)).stream()
                    .findFirst();

            if (collection.isPresent()) {
                //return XML.toString(JSON.parse(((BasicDBObject) collection.get().get(DOCUMENT)).toJson()));
                return (String) collection.get().get(DOCUMENT);
            }
        } catch (final MetaDataServiceException e) {
            LOG.error("Error retrieving parser for name {} type {}", name, type, e);
        } catch (final Exception e) {
            LOG.error("Unknow Error while retrieving document for name {}, type {}", name, type, e);
        }
        return null;
    }

    @Override
    public void deleteParser(final String parserId) {

        Assert.notNull(parserId, "id can not be null");

        try {
            metaDataService.delete(modelCatalogName, parserId);
        } catch (final MetaDataServiceException e) {
            throw new ModelManagerException(ModelManagerExceptionCodes.ERROR_INVOKING_METADATASERVICE_ON_DELETE, e);
        }

    }

    @Override
    public void updateParser(final String parserId, final String parser) {

        Assert.notNull(parserId, "id can not be null");
        Assert.notNull(parser, "parser not be null");

        try {
            final Map<String, Object> fields = new HashMap<>();
            fields.put(DOCUMENT, parser);
            metaDataService.updateDocument(modelCatalogName, parserId, fields);

        } catch (final MetaDataServiceException e) {
            throw new ModelManagerException(ModelManagerExceptionCodes.ERROR_INVOKING_METADATASERVICE_ON_UPDATE, e);
        }

    }

    @Override
    public String addParser(final String name, final String type, final String parser) {

        Assert.notNull(name, "name not be null");
        Assert.notNull(type, "type not be null");
        Assert.notNull(parser, "parser not be null");

        try {
            final Collection<Map<String, Object>> colls = this.metaDataService.findByFilter(modelCatalogName,
                    FilterBuilder.newMongoDBFilter().addProperty(TYPE, type).addProperty(NAME, name).build(), Arrays.asList(KEY));

            if (!colls.isEmpty()) {
                throw new ParserAlreadyExistsException(ModelManagerExceptionCodes.MODEL_ALREADY_EXISTS,
                        String.format("Model %s for type %s already exists!", name, type));
            }
        } catch (final MetaDataServiceException e) {
            throw new ModelManagerException(ModelManagerExceptionCodes.ERROR_INVOKING_METADATASERVICE_ON_GET, e);
        }

        try {

            final Map<String, Object> documentMap = new HashMap<>();

            final String modelId = ModelManagerUtil.createModelId(name, type, null);

            documentMap.put(DOCUMENT, parser);
            documentMap.put(TYPE, type);
            documentMap.put(NAME, name);
            documentMap.put(KEY, modelId);

            metaDataService.put(modelCatalogName, documentMap);

            return modelId;

        } catch (final MetaDataServiceException e) {
            throw new ModelManagerException(ModelManagerExceptionCodes.ERROR_INVOKING_METADATASERVICE_ON_SAVE, e);
        }

    }

    @Override
    public Collection<String> listParsersByType(final String type) {

        final Collection<String> list = new ArrayList<>();

        try {
            final Collection<Map<String, Object>> collection = this.metaDataService.findByFilter(modelCatalogName,
                    FilterBuilder.newMongoDBFilter().addFilter(TYPE, Operator.EQ_IGNORE_CASE, type).build(), Arrays.asList(NAME));

            collection.stream().forEach(map -> list.add((String) map.get(NAME)));

        } catch (final MetaDataServiceException e) {
            LOG.error("Error retrieving parser for type {}", type, e);
        } catch (final Exception e) {
            LOG.error("Unknow error while retrieving the document for type {}", type, e);
        }
        return list;
    }

    /**
     * Builds the modelManager
     *
     * @return the ModelManagerBuilder Implementation
     */
    public static ModelManagerImplBuilder builder() {
        return new ModelManagerImplBuilder();
    }

    @Override
    public Collection<String> listAllParserTypes() {
        final Collection<String> set = new HashSet<>();

        try {
            final Collection<Map<String, Object>> collection = this.metaDataService.findByFilter(modelCatalogName, null, Arrays.asList(TYPE));

            collection.stream().forEach(map -> set.add((String) map.get(TYPE)));

        } catch (final MetaDataServiceException e) {
            LOG.error("Error retrieving all parsers types", e);
        } catch (final Exception e) {
            LOG.error("Unknow error while retrieving all documents by type ", e);
        }
        return set;
    }

    @Override
    public boolean validate(final String xml) {

        Assert.notNull(xml, "xml can not be null");

        try {

            LOG.debug("Validating XML {}" + xml);

            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);

            final DocumentBuilder builder = factory.newDocumentBuilder();
            builder.parse(new ByteArrayInputStream(xml.getBytes()));

            return true;

        } catch (final Exception ex) {
            LOG.error(ex.getMessage());
            return false;
        }
    }

}

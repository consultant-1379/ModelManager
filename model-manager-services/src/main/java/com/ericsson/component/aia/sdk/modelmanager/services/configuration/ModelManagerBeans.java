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
package com.ericsson.component.aia.sdk.modelmanager.services.configuration;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.ericsson.aia.metadata.api.MetaDataServiceIfc;
import com.ericsson.aia.metadata.exception.MetaDataServiceException;
import com.ericsson.aia.metadata.lifecycle.MetaDataServiceLifecycleManagerIfc;
import com.ericsson.aia.metadata.lifecycle.impl.MetaDataServiceLifecycleManagerImpl;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class contains the basic beans that make up the application manager
 */
@Configuration
@Profile(value = { "container", "local", "production" })
public class ModelManagerBeans {

    @Autowired
    protected ModelManagerServiceConfig applicationManagerServiceConfig;

    @Autowired
    protected MetaStoreConfig metaStoreConfig;

    /**
     * instantiates an application manager beans configuration
     */
    public ModelManagerBeans() {
    }

    /**
     * instantiates an application manager beans configuration
     *
     * @param applicationManagerServiceConfig
     *            The application manager service configuration.
     * @param metaStoreConfig
     *            The meta store configuration.
     */
    public ModelManagerBeans(final ModelManagerServiceConfig applicationManagerServiceConfig, final MetaStoreConfig metaStoreConfig) {
        super();
        this.applicationManagerServiceConfig = applicationManagerServiceConfig;
        this.metaStoreConfig = metaStoreConfig;
    }

    /**
     * This method creates {@link ObjectMapper} bean.
     *
     * @return {@link ObjectMapper} bean.
     */
    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    /**
     * This method creates {@link MetaDataServiceIfc} bean.
     *
     * @return {@link MetaDataServiceIfc} bean.
     * @throws MetaDataServiceException
     *             if {@link MetaDataServiceIfc} bean creation fails.
     * @throws IOException
     *             if {@link MetaDataServiceIfc} bean creation fails.
     */
    @Bean
    public MetaDataServiceIfc metaDataServiceIfc() throws MetaDataServiceException, IOException {
        final MetaDataServiceLifecycleManagerIfc serviceLifecycleManager = new MetaDataServiceLifecycleManagerImpl();
        serviceLifecycleManager.provisionService(metaStoreConfig.getMetaStoreProperties());
        final MetaDataServiceIfc metaDataService = serviceLifecycleManager.getServiceReference();

        final String modelCatalogName = applicationManagerServiceConfig.getModelCatalogName();

        if (!metaDataService.schemaExists(modelCatalogName)) {
            metaDataService.createSchema(modelCatalogName);
        }
        return metaDataService;
    }

}

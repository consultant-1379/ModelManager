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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ericsson.component.aia.sdk.modelmanager.core.config.ModelManagerConfiguration;

/**
 * Application manager service configuration
 *
 * TODO Using the tag below to suppress PMD TooManyFields error. Sort this out.
 */

@Component
public class ModelManagerServiceConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelManagerServiceConfig.class);

    @Value("${storage.location}")
    private String storageLocation;

    @Value("${model.catalog.name}")
    private String modelCatalogName;

    /**
     * Print the parameters being used to start the spring application
     */
    public void printConfiguration() {

        LOGGER.info("modelCatalogName::{}", modelCatalogName);

        LOGGER.info("storageLocation::{}", storageLocation);

    }

    /**
     * This method will update the ApplicationManagerConfiguration to include all application properties.
     */
    public void updateApplicationManagerConfiguration() {
        ModelManagerConfiguration.modelCatalogName = modelCatalogName;

        ModelManagerConfiguration.storageLocation = storageLocation;

    }

    public String getModelCatalogName() {
        return modelCatalogName;
    }

}

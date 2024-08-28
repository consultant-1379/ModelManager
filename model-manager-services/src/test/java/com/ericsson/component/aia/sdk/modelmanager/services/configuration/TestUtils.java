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
package com.ericsson.component.aia.sdk.modelmanager.services.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ericsson.aia.metadata.api.MetaDataServiceIfc;
import com.ericsson.aia.metadata.exception.MetaDataServiceException;
import com.ericsson.aia.metadata.model.MetaData;

/**
 * 
 * @author ezsalro
 *
 */
@Component
public class TestUtils {

    @Value("${model.catalog.name}")
    private String modelCatalogName;

    @Autowired
    private MetaDataServiceIfc metaDataService;

    /**
     * This method will erase all entries from meta store
     *
     * @throws MetaDataServiceException
     */
    public void clearMetaStore() throws MetaDataServiceException {
        for (final MetaData metaData : metaDataService.findAll(modelCatalogName)) {
            if (!metaData.getKey().isEmpty()) {
                metaDataService.delete(modelCatalogName, metaData.getKey());
            }
        }
    }

}

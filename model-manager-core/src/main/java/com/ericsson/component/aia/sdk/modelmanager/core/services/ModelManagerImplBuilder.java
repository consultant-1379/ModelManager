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

import com.ericsson.aia.metadata.api.MetaDataServiceIfc;
import com.ericsson.component.aia.sdk.modelmanager.exceptions.ModelManagerException;
import com.ericsson.component.aia.sdk.modelmanager.exceptions.ModelManagerExceptionCodes;

/**
 * Builder class for creating {@link ApplicationManagerImpl}
 *
 */
public class ModelManagerImplBuilder {

    private MetaDataServiceIfc metaDataServiceManager;

    /**
     * This method will set the {@link MetaDataServiceIfc} reference.
     *
     * @param metaDataServiceManager
     *            {@link MetaDataServiceIfc} reference.
     * @return current {@link ModelManagerImplBuilder} reference
     */
    public ModelManagerImplBuilder metaDataServiceManager(final MetaDataServiceIfc metaDataServiceManager) {
        this.metaDataServiceManager = metaDataServiceManager;
        return this;
    }

    /**
     * This method will create new instance of {@link ApplicationManagerImpl}.
     *
     * @return new instance of {@link ApplicationManagerImpl}.
     */
    public ModelManagerImpl build() {

        if (metaDataServiceManager == null) {
            throw new ModelManagerException(ModelManagerExceptionCodes.ERROR_REGISTERING_SERVICES, "MetaDataServiceIfc is null");
        }

        return new ModelManagerImpl(metaDataServiceManager);
    }
}

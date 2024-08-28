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

import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.ResourceNotFoundException;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.exception.SchemaException;

/**
 * @author aia
 */
public enum SchemaProviderType {
    FILE_BASED {

        @Override
        public SchemaProvider getSchemaProvider(final SchemaEnum schemaEnum) throws ResourceNotFoundException, SchemaException {
            return new FileBasedSchemaHandler(schemaEnum).load();
        }
    },
    MODEL_DRIVEN_FILE_BASED {

        @Override
        public SchemaProvider getSchemaProvider(final SchemaEnum schemaEnum) throws ResourceNotFoundException, SchemaException {
            return new ModelDrivenFileBasedSchemaHandler(schemaEnum).load();
        }
    },
    MODEL_DRIVEN_FILE_BASED_ON_DEMAND {

        @Override
        public SchemaProvider getSchemaProvider(final SchemaEnum schemaEnum) throws ResourceNotFoundException, SchemaException {
            return new ModelDrivenFileBasedLazySchemaHandler(schemaEnum).load();
        }
    },
    /**
     * {@link FILE_BASED_ON_DEMAND} provider is similar to {@link FILE_BASED}, it that reads schemas from files. <br>
     * Comparing to {@link FILE_BASED} this provider does not load all schemas into memory at initialization but only when they are actually needed to
     * decode a file. It gives better performance for short living applications as only needed 101/ files are parsed.
     */
    FILE_BASED_ON_DEMAND {

        @Override
        public SchemaProvider getSchemaProvider(final SchemaEnum schemaEnum) throws ResourceNotFoundException, SchemaException {
            return new FileBasedLazyLoadingSchemaHandler(schemaEnum).load();
        }
    };

    /**
     * @param enumStringValue
     *            - enum string
     * @return schema provider
     */
    public static SchemaProviderType fromValue(final String enumStringValue) {
        final String typeInUpperCase = enumStringValue.toUpperCase().trim();
        for (final SchemaProviderType schemaProviderType : SchemaProviderType.values()) {
            if (schemaProviderType.toString().equals(typeInUpperCase)) {
                return schemaProviderType;
            }
        }
        throw new IllegalArgumentException(enumStringValue);
    }

    /**
     * @param schemaEnum
     *            - schema
     * @return schema
     * @throws ResourceNotFoundException
     *             - exception
     * @throws SchemaException
     *             - exception
     */
    public abstract SchemaProvider getSchemaProvider(final SchemaEnum schemaEnum) throws ResourceNotFoundException, SchemaException;

}

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
package com.ericsson.component.aia.sdk.modelmanager.core.util;

import org.apache.commons.lang3.StringUtils;

/**
 * This class provides utility methods for model manager
 *
 * @author ezsalro
 *
 */
public final class ModelManagerUtil {

    private static final String TAG_SEPARATOR = ":";
    private static final String MODEL_TAG = "model";

    private ModelManagerUtil() {

    }

    /**
     * Creates the model ID.
     *
     * @param name
     *            - name of model
     * @param type
     *            - type of model
     * @param version
     *            - optional version
     * @return the generated ID.
     */
    public static String createModelId(final String name, final String type, final String version) {

        final StringBuilder modelId = new StringBuilder(MODEL_TAG).append(TAG_SEPARATOR).append(type.toLowerCase()).append(TAG_SEPARATOR)
                .append(name.toLowerCase());

        if (StringUtils.isNotEmpty(version)) {
            modelId.append(TAG_SEPARATOR).append(version.toLowerCase());
        }

        return modelId.toString();

    }

}

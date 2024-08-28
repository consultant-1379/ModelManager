/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.DefaultValues;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.EventParameter;

/**
 * @author aia
 */
public class LongParameterDecoder extends GenericParameterDecoder {

    /**
     * @param eventParameter
     *            - event parameter
     */
    public LongParameterDecoder(final EventParameter eventParameter) {
        super(eventParameter);
    }

    @Override
    public Long decode(final byte[] data, final int offset) {
        return dataConverterHelper.getByteArrayUnsignedInteger(data, offset, eventParameter.getNumberOfBytes());
    }

    @Override
    public Object getDefaultValueForGenericRecord() {
        return DefaultValues.DEFAULT_LONG_VALUE;
    }

}
